<script lang="ts">
	import { getLocale } from '$lib/paraglide/runtime';
	import Table, { type Column } from '$components/Table/Table.svelte';
	import ButtonCell from '$components/Table/ButtonCell.svelte';
	import { m } from '$lib/paraglide/messages';
	import { type Doctor, type File, type Study } from '$types/api';
	import type { PageData } from './$types';
	import { goto } from '$app/navigation';
	import { base } from '$app/paths';
	import Button from '$components/Button/Button.svelte';
	import { parseDateInLocalTimezone } from '$lib/services/appointments';
	import {
		authorizeDoctorsForStudy,
		deleteStudy,
		unauthorizeDoctorsForStudy
	} from '$lib/services/studies';
	import { fetchFilesPage } from '$lib/services/files';
	import { fetchWithAuth } from '$modules/api.svelte';
	import Icon from '$components/Icon/Icon.svelte';
	import Toast from '$components/Toast/Toast.svelte';
	import JSZip from 'jszip';
	import PopUp from '$components/PopUp/PopUp.svelte';

	let { data }: { data: PageData } = $props();

	let study: Study = $state(data.study!);
	let doctors: Doctor[] = $state(data.doctors ?? []);
	let authorizedDoctorsEmails: string[] = $state(data.authorizedDoctorsEmails ?? []);

	let viewingFile: File | null = $state(null);
	let viewingFileBlobUrl: string | null = $state(null);

	$effect(() => {
		if (viewingFile) {
			viewingFileBlobUrl = null;
			fetchWithAuth(viewingFile.links.self.resolved!, { headers: { Accept: viewingFile.type } })
				.then(async (response) => {
					if (response.ok) {
						const blob = await response.blob();
						viewingFileBlobUrl = window.URL.createObjectURL(blob);
					}
				}).catch((err) => console.error('Failed to load file for viewing:', err));
		} else {
			if (viewingFileBlobUrl) {
				window.URL.revokeObjectURL(viewingFileBlobUrl);
				viewingFileBlobUrl = null;
			}
		}
	});

	// Extract file ID from API URL
	const getFileId = (fileUrl: string): string => {
		const match = fileUrl.match(/\/files\/(\d+)$/);
		return match ? match[1] : '';
	};

	let confirmDelete = $state(false);
	let showError = $state(false);
	let errorTitle = $state('');
	let errorMessage = $state('');

	$effect(() => {
		if (!showError) {
			errorTitle = '';
			errorMessage = '';
		}
	});

	const unauthorizeDoctor = async (doctor: Doctor) => {
		unauthorizeDoctorsForStudy(study.links.self.resolved!, [doctor.links.self.resolved!], fetch)
			.then(() => {
				if (authorizedDoctorsEmails.includes(doctor.email)) {
					authorizedDoctorsEmails = authorizedDoctorsEmails.filter(
						(email) => email !== doctor.email
					);
				} else {
					authorizedDoctorsEmails = [...authorizedDoctorsEmails, doctor.email];
				}
			})
			.catch((error) => {
				console.error(error);
				errorTitle = m['study_info.error.authorizing.title']();
				errorMessage = m['study_info.error.authorizing.message']({ doctorName: doctor.name });
				showError = true;
			});
	};

	const authorizeDoctor = async (doctor: Doctor) => {
		authorizeDoctorsForStudy(study.links.self.resolved!, [doctor.links.self.resolved!], fetch)
			.then(() => {
				if (authorizedDoctorsEmails.includes(doctor.email)) {
					authorizedDoctorsEmails = authorizedDoctorsEmails.filter(
						(email) => email !== doctor.email
					);
				} else {
					authorizedDoctorsEmails = [...authorizedDoctorsEmails, doctor.email];
				}
			})
			.catch((error) => {
				console.error(error);
				errorTitle = m['study_info.error.authorizing.title']();
				errorMessage = m['study_info.error.authorizing.message']({ doctorName: doctor.name });
				showError = true;
			});
	};

	const fileColumns: Column<File>[] = [
		{
			id: 'type',
			label: m['study_info.table.file_type'](),
			render: (file: File) => {
				return m[`study_info.file_types.${file.type.replace('/', '_')}`]();
			},
			class: 'font-medium'
		},
		{
			id: 'actions',
			label: m['actions'](),
			render: (file: File) => {
				return {
					component: ButtonCell,
					props: {
						text: 'eye download',
						icon: true,
						variant: ['success', 'primary'],
						onclick: async (event: MouseEvent, index?: number) => {
							event.stopPropagation();
							try {
								const response = await fetchWithAuth(file.links.self.resolved!, { headers: { Accept: file.type } });

								if (!response.ok) throw new Error('Download failed');

								const blob = await response.blob();
								const blobUrl = window.URL.createObjectURL(blob);

								if (index) {
									// download file
									const contentDisposition = response.headers.get('Content-Disposition');
									let filename = study.comment.replace(/\s+/g, '-') + '_' + file.type.split('/')[1];
									if (contentDisposition) {
										const filenameMatch = contentDisposition.match(
											/filename[^;=\n]*=(['"]?)([^'"\n]*)\1/
										);
										if (filenameMatch) filename = filenameMatch[2];
									}

									const link = document.createElement('a');
									link.href = blobUrl;
									link.download = filename;
									link.style.display = 'none';
									document.body.appendChild(link);
									link.click();

									setTimeout(() => {
										document.body.removeChild(link);
										window.URL.revokeObjectURL(blobUrl);
									}, 100);
								} else {
									// view file in new tab
									window.open(blobUrl, '_blank');
								}
							} catch (error) {
								console.error('Download error:', error);
							}
						},
						class: 'px-2 py-1 text-sm font-semibold'
					}
				};
			},
			class: 'flex gap-2',
			columnClass: 'w-25'
		}
	];

	const downloadAllFiles = async () => {
		let allFiles = study.files?.results ?? [];
		let files = study.files;

		while (files?._links.next) {
			const newPage = await fetchFilesPage(files._links.next, fetch).catch((error) => {
				console.error('Error fetching files:', error);
			});

			if (newPage) {
				allFiles = [...allFiles, ...newPage.results];
				files = newPage;
			}
		}

		// Put all files in a zip and download it
		const zip = new JSZip();
		const folder = zip.folder(study.comment.replace(/\s+/g, '_'));
		if (!folder) {
			return;
		}

		let i = 0;
		for (const file of allFiles) {
			try {
				const response = await fetchWithAuth(file.links.self.resolved!);
				if (!response.ok) throw new Error('Download failed');

				const blob = await response.blob();
				folder.file(`${i}_${file.type}`, blob);
				i++;
			} catch (error) {
				console.error('Error downloading file:', error);
				errorTitle = m['study_info.error.downloading.title']();
				errorMessage = m['study_info.error.downloading.message']();
				showError = true;
				return;
			}
		}

		const content = await zip.generateAsync({ type: 'blob' });
		const url = window.URL.createObjectURL(content);
		const link = document.createElement('a');
		link.href = url;
		link.download = study.comment.replace(/\s+/g, '_') + '.zip';
		link.style.display = 'none';
		document.body.appendChild(link);
		link.click();

		setTimeout(() => {
			document.body.removeChild(link);
			window.URL.revokeObjectURL(url);
		}, 100);
	};

	const doctorColumns: Column<Doctor>[] = [
		{
			id: 'name',
			label: m['studies.doctors.table.name'](),
			render: (doctor: Doctor) => doctor.name,
			class: 'font-medium'
		},
		{
			id: 'specialty',
			label: m['studies.doctors.table.specialty'](),
			render: (doctor: Doctor) => m[`specialties.${doctor.specialty}`](),
			class: 'text-start'
		},
		{
			id: 'doctor-action',
			label: m['action'](),
			render: (doctor: Doctor, index: number) => {
				return {
					component: ButtonCell,
					props: {
						text: authorizedDoctorsEmails.includes(doctor.email)
							? m['study_info.doctors.actions.revoke']()
							: m['study_info.doctors.actions.grant'](),
						variant: authorizedDoctorsEmails.includes(doctor.email) ? 'destructive' : 'success',
						onclick: (e: MouseEvent) => {
							e.stopPropagation();
							e.preventDefault();

							if (authorizedDoctorsEmails.includes(doctor.email)) {
								unauthorizeDoctor(doctor);
							} else {
								authorizeDoctor(doctor);
							}
						},
						class: 'px-2 py-1 text-xs! font-semibold'
					}
				};
			},
			class: 'flex justify-center items-center text-center',
			columnClass: 'w-28'
		}
	];

	const handleDeauthorizeAll = async () => {
		for (const doctor of doctors) {
			if (authorizedDoctorsEmails.includes(doctor.email)) {
				await unauthorizeDoctorsForStudy(study.links.self.resolved!, [doctor.links.self.resolved!], fetch)
					.then(() => {
						authorizedDoctorsEmails = authorizedDoctorsEmails.filter(
							(email) => email !== doctor.email
						);
					})
					.catch((error) => {
						console.error(error);
						errorTitle = m['study_info.error.authorizing.title']();
						errorMessage = m['study_info.error.authorizing.message']({ doctorName: doctor.name });
						showError = true;
					});
			}
		}
	};

	const handleAuthorizeAll = async () => {
		for (const doctor of doctors) {
			if (!authorizedDoctorsEmails.includes(doctor.email)) {
				await authorizeDoctorsForStudy(study.links.self.resolved!, [doctor.links.self.resolved!], fetch)
					.then(() => {
						authorizedDoctorsEmails = [...authorizedDoctorsEmails, doctor.email];
					})
					.catch((error) => {
						console.error(error);
						errorTitle = m['study_info.error.authorizing.title']();
						errorMessage = m['study_info.error.authorizing.message']({ doctorName: doctor.name });
						showError = true;
					});
			}
		}
	};
</script>

<div class="flex gap-5">
	<div class="page-division flex flex-col h-full w-full max-w-100 gap-2.5 select-text">
		<h1 class="text-[24px] font-bold mb-2.5">{study.comment}</h1>
		<p class="text-line text-primaryText">
			<span class="font-bold select-none">{m['study_info.labels.type']()}:</span>
			{m[`studies.types.options.${study.type?.replace(/\s/g, '_').toLowerCase()}`]()}
		</p>
		<p class="text-line text-primaryText">
			<span class="font-bold select-none">{m['study_info.labels.date']()}:</span>
			{parseDateInLocalTimezone(study.studyDate).toLocaleDateString(getLocale(), {
				year: 'numeric',
				month: '2-digit',
				day: '2-digit'
			})}
		</p>
		<p class="text-line text-primaryText">
			<span class="font-bold select-none">{m['study_info.labels.upload_date']()}:</span>
			{parseDateInLocalTimezone(study.uploadDate).toLocaleDateString(getLocale(), {
				year: 'numeric',
				month: '2-digit',
				day: '2-digit'
			})}
		</p>
		<p class="text-line text-primaryText">
			<span class="font-bold select-none">{m['study_info.labels.uploader']()}:</span>
			{study.uploaderName ? study.uploaderName : m['unknown']()}
		</p>
		{#if data.currentUser.role === 'PATIENT'}
			<Button
				variant="destructive"
				class="w-fit"
				onclick={() => {
					confirmDelete = true;
				}}
			>
				<Icon name="trash" class="w-5 h-5 mr-1" />
				{m['study_info.actions.delete']()}
			</Button>
		{/if}
	</div>

	<div class="page-division flex flex-col h-full w-full gap-2.5">
		<div class="flex flex-row justify-between items-center w-full h-fit">
			<h1 class="text-[24px] font-bold mb-2.5">{m['study_info.titles.files']()}:</h1>
			<div class="flex gap-2">
				<Button variant="primary" class="w-fit text-[14px]!" onclick={downloadAllFiles}>
					{m['study_info.actions.download_all']()}
				</Button>
			</div>
		</div>
		<Table
			rows={study.files!}
			columns={fileColumns}
			nextFetchFunction={fetchFilesPage}
			striped
			bordered
			hover
			onRowClick={(file: File) => {
				viewingFile = file;
			}}
		/>
	</div>

	{#if data.currentUser.role === 'PATIENT'}
		<div class="page-division flex flex-col h-full w-full gap-2.5">
			<div class="flex flex-row justify-between items-center w-full h-fit">
				<h1 class="text-[24px] font-bold mb-2.5">{m['study_info.titles.doctors']()}:</h1>
				<div class="flex gap-2">
					{#if authorizedDoctorsEmails.length < doctors.length}
						<Button variant="success" class="w-fit text-[14px]!" onclick={handleAuthorizeAll}>
							{m['study_info.doctors.actions.grant_all']()}
						</Button>
					{/if}
					{#if authorizedDoctorsEmails.length > 0}
						<Button variant="destructive" class="w-fit text-[14px]!" onclick={handleDeauthorizeAll}>
							{m['study_info.doctors.actions.revoke_all']()}
						</Button>
					{/if}
				</div>
			</div>
			<Table rows={doctors} columns={doctorColumns} striped bordered hover />
		</div>
	{/if}

	<!-- File view PopUp -->
	{#if viewingFile}
		<div
			class="fixed inset-0 z-1001 bg-black/75 p-15 pb-5 flex items-center justify-center"
			onclick={() => (viewingFile = null)}
			onkeydown={(e) => {
				if (e.key === 'Escape') {
					viewingFile = null;
				}
			}}
			aria-modal="true"
			role="dialog"
			tabindex="-1"
		>
			<button
				class="absolute bg-[#282828] px-3 py-1 rounded-xl top-2.5 right-2.5 text-white text-4xl hover:text-gray-300 hover:bg-black cursor-pointer z-10"
				onclick={() => (viewingFile = null)}
			>
				&times;
			</button>
			{#if !viewingFileBlobUrl}
				<div class="flex w-full h-full items-center justify-center">
					<p class="text-white text-lg">Cargando...</p>
				</div>
			{:else if viewingFile.type.startsWith('image/')}
				<!-- svelte-ignore a11y_role_supports_aria_props -->
				<!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
				<div
					class="flex w-full h-full bg-[#282828] items-center justify-center rounded-2xl shadow-lg p-4"
					onclick={(e) => e.stopPropagation()}
					onkeydown={(e) => {
                        if (e.key === 'Escape') e.stopPropagation()
                    }}
                    aria-modal="false"
                    role="document"
                    tabindex="-2"
				>
					<img
						src={viewingFileBlobUrl}
						alt="File preview"
						class="max-w-full max-h-full object-contain rounded-lg shadow-lg"
					/>
				</div>
				<!-- svelte-ignore element_invalid_self_closing_tag -->
			{:else if viewingFile.type === 'application/pdf'}
				<!-- svelte-ignore a11y_click_events_have_key_events -->
				<!-- svelte-ignore a11y_role_supports_aria_props -->
				<!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
				<!-- svelte-ignore element_invalid_self_closing_tag -->
				<iframe
					src={viewingFileBlobUrl}
					title="PDF Viewer"
					class="w-full h-full rounded-lg shadow-lg"
					onclick={(e) => e.stopPropagation()}
					role="document"
				/>
			{/if}
		</div>
	{/if}

	{#if confirmDelete}
		<PopUp onClose={() => (confirmDelete = false)}>
			<div class="flex flex-col gap-4">
				<h2 class="text-xl font-bold">{m['studies.pop_up.delete.title']()}</h2>
				<div class="flex flex-col gap-1.5">
					<p>
						{m['studies.pop_up.delete.date_info']({
							uploadDate: new Date(study.uploadDate).toLocaleDateString(getLocale(), {
								year: 'numeric',
								month: '2-digit',
								day: '2-digit'
							})
						})}
					</p>
					<p class="font-semibold">
						{m['studies.pop_up.delete.comment_label']()}
						<span class="font-normal select-text"> {study.comment}</span>
					</p>
				</div>
				<p class="text-red-600 font-semibold">{m['studies.pop_up.delete.subtitle']()}</p>
				<div class="flex justify-end gap-2 mt-4">
					<Button
						variant="primary"
						onclick={() => {
							deleteStudy(study.links.self.resolved!, fetch)
								.then(() => {
									goto(`${base}/studies`);
								})
								.catch((error) => {
									console.error(error);
									errorTitle = m['study_info.error.deleting.title']();
									errorMessage = m['study_info.error.deleting.message']();
									showError = true;
								});
						}}
					>
						{m['studies.pop_up.delete.confirm']()}
					</Button>
					<Button variant="destructive" onclick={() => (confirmDelete = false)}>
						{m['studies.pop_up.delete.cancel']()}
					</Button>
				</div>
			</div>
		</PopUp>
	{/if}

	<Toast
		bind:show={showError}
		title={errorTitle}
		description={errorMessage}
		variant="destructive"
		duration={3000}
	/>
</div>
