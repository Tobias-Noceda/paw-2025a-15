<script lang="ts">
	import { getLocale } from '$lib/paraglide/runtime';
	import Table, { type Column } from '$components/Table/Table.svelte';
	import ButtonCell from '$components/Table/ButtonCell.svelte';
	import { m } from '$lib/paraglide/messages';
	import { baseApiUrl, type Doctor, type File, type Paginated, type Study } from '$types/api';
	import type { PageData } from './$types';
	import { goto } from '$app/navigation';
	import { base } from '$app/paths';
	import Button from '$components/Button/Button.svelte';
	import { parseDateInLocalTimezone } from '$lib/services/appointments';
	import { fetchFilesPage, fetchStudies, fetchStudiesPage } from '$lib/services/studies';
	import { fetchDoctorsPage, putAuthorizations } from '$lib/services/doctors';
	import Icon from '$components/Icon/Icon.svelte';

	let { data }: { data: PageData } = $props();

	let study: Study = $state(data.study!);
	let doctors: Doctor[] = $state(data.doctors ?? []);
	let authorizedDoctorsEmails: string[] = $state(data.authorizedDoctorsEmails ?? []);

	// parse self (cut after /api/)
	const parseDoctor = (self: string) => {
		const limitString = '/api/';
		const apiIndex = self.indexOf(limitString);
		const toRet = apiIndex !== -1 ? self.substring(apiIndex + limitString.length) : self;
		return toRet;
	};

	const fileColumns: Column<File>[] = [
		{
			id: 'type',
			label: m['study_info.table.file_type'](),
			render: (file: File) => m[`study_info.file_types.${file.type.replace('/', '_')}`](),
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
                        onclick: async (event, index) => {
                            event.stopPropagation();
                            if (index) {
                                // download file
                                try {
                                    const response = await fetch(file.links.self, {
                                        credentials: 'include',
                                        headers: {
                                            'Accept': file.type
                                        }
                                    });
                                    
                                    if (!response.ok) throw new Error('Download failed');
                                    
                                    const blob = await response.blob();
                                    
                                    // Get filename from Content-Disposition header or use file type
                                    const contentDisposition = response.headers.get('Content-Disposition');
                                    let filename = study.comment.replace(/\s+/g, '-') + '_' + file.type.split('/')[1];
                                    if (contentDisposition) {
                                        const filenameMatch = contentDisposition.match(/filename[^;=\n]*=(['"]?)([^'"\n]*)\1/);
                                        if (filenameMatch) filename = filenameMatch[2];
                                    }
                                    
                                    const url = window.URL.createObjectURL(blob);
                                    const link = document.createElement('a');
                                    link.href = url;
                                    link.download = filename;
                                    link.style.display = 'none';
                                    document.body.appendChild(link);
                                    link.click();
                                    
                                    // Cleanup after a short delay
                                    setTimeout(() => {
                                        document.body.removeChild(link);
                                        window.URL.revokeObjectURL(url);
                                    }, 100);
                                } catch (error) {
                                    console.error('Download error:', error);
                                    // Fallback: open in new window
                                    window.open(file.links.self, '_blank');
                                }
                            } else {
                                // view file in new tab
                                window.open(file.links.self, '_blank');
                            }
                        },
                        class: 'px-2 py-1 text-sm font-semibold'
                    }
                }
            },
			class: 'flex gap-2',
            columnClass: 'w-25'
		}
	];

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
						text: authorizedDoctorsEmails.includes(doctor.email) ? m['study_info.doctors.actions.revoke']() : m['study_info.doctors.actions.grant'](),
						variant: authorizedDoctorsEmails.includes(doctor.email) ? 'destructive' : 'success',
						onclick: (e: MouseEvent) => {
							e.stopPropagation();
							e.preventDefault();

							putAuthorizations(doctor.links.authorization.resolved!, false, [])
                                .catch((error) => {
                                    console.error(`Error deauthorizing doctor ${doctor.name}:`, error);
                                });

                            doctors.results.splice(index, 1);
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
        console.log('Deauthorizing all doctors:', authorizedDoctorsEmails);
        authorizedDoctorsEmails = [];
	};
    
	const handleAuthorizeAll = async () => {
        console.log('Authorizing all doctors:', authorizedDoctorsEmails);
        authorizedDoctorsEmails = doctors.map(doctor => doctor.email);
	};
</script>

<div class="flex gap-5">
	<div class="page-division flex flex-col h-full w-full max-w-100 gap-2.5 select-text">
        <h1 class="text-[24px] font-bold mb-2.5">{study.comment}</h1>
        <p class="text-line text-primaryText">
            <span class="font-bold select-none">{m['study_info.labels.type']()}:</span>
            {m[`studies.types.options.${study.type.toLocaleLowerCase()}`]()}
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
                    console.log('Delete study with:', study);
                    // deleteStudy(study.links.self, data.currentUser, fetch)
                    //     .then(() => {
                    //         goto(`${base}/studies`);
                    //     })
                    //     .catch((error) => {
                    //         console.error('Error deleting study:', error);
                    //     });
                }}
            >
                <Icon name="trash" class="w-5 h-5 mr-1" />
                {m['study_info.actions.delete']()}
            </Button>
        {/if}
	</div>

	<div class="page-division flex flex-col h-full w-full gap-2.5">
		<h1 class="text-[24px] font-bold mb-2.5">{m['study_info.titles.files']()}:</h1>
        <Table 
            rows={study.files!}
            columns={fileColumns}
            nextFetchFunction={fetchFilesPage}
            striped
            bordered
            hover
            onRowClick={(file: File) => {
                // view file in new tab
                window.open(file.links.self, '_blank');
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
            <Table 
                rows={doctors}
                columns={doctorColumns}
                striped
                bordered
                hover
                onRowClick={(doctor: Doctor) => {
                    const doctorPath = parseDoctor(doctor.links.self);
                    if (doctorPath) {
                        goto(`${base}/${doctorPath}`);
                    }
                }}
            />
        </div>
    {/if}
</div>
