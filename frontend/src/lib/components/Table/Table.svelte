<script lang="ts">
	import { cn } from '$lib/utils';
	import { m } from '$lib/paraglide/messages.js';
	import ScrollPagination from '$components/ScrollPagination/ScrollPagination.svelte';
	import type { Paginated } from '$types/api';

	export interface Column<RowType> {
		id: keyof RowType | string;
		label: string;
		render?: (row: RowType, index: number) => any;
		class?: string;
		columnClass?: string;
	}

	interface Props<RowType> {
		rows: RowType[] | Paginated<RowType>;
		nextFetchFunction?: (nextUrl: string) => Promise<Paginated<RowType>>;
		columns: Column<RowType>[];
		striped?: boolean;
		bordered?: boolean;
		hover?: boolean;
		skeleton?: boolean;
		emptyMessage?: string;
		onRowClick?: (row: RowType, index: number) => void;
		class?: string;
	}

	let {
		rows = [],
		columns,
		striped = false,
		bordered = false,
		hover = false,
		skeleton = false,
		emptyMessage = m.no_data_available({}),
		nextFetchFunction,
		onRowClick,
		class: tableClass = ''
	}: Props<any> = $props();

	const containerClass = cn(
		'flex flex-col w-full h-full overflow-hidden',
		bordered ? 'shadow-lg rounded-lg' : '',
		tableClass
	);
</script>

{#snippet loader(count: number)}
	{#each Array(count) as _, i}
		<tr class={cn(i !== 4 ? 'border-b border-gray-200' : '', 'last:rounded-b-lg')}>
			{#each columns as col}
				<td class={'px-3 py-2 ' + (col.class ?? '')}>
					<div class="w-full h-4 bg-skeleton animate-pulse rounded-md"></div>
				</td>
			{/each}
		</tr>
	{/each}
{/snippet}

<div class={containerClass}>
	<div class="h-full overflow-hidden">
		<div
			class="h-full overflow-y-auto scrollbar-thin scrollbar-thumb-table-header scrollbar-track-transparent"
		>
			<table class="w-full table-fixed border-collapse">
				<thead class="bg-table-header text-white sticky top-0 z-10 cursor-default! select-none">
					<tr>
						{#each columns as col}
							<th
								class={'text-left px-3 py-2 font-semibold first:rounded-tl-lg last:rounded-tr-lg ' +
									(col.columnClass ?? '')}
							>
								{col.label}
							</th>
						{/each}
					</tr>
				</thead>

				<tbody>
					{#if skeleton}
						{@render loader(
							Math.min(5, Math.max(2, Array.isArray(rows) ? rows.length : rows.results.length))
						)}
						<!-- if nextFetchFunction is defined and rows is Paginated -->
					{:else if nextFetchFunction && !Array.isArray(rows) && rows.results.length > 0}
						{#key rows.results.length}
							<ScrollPagination initialItems={rows} {nextFetchFunction}>
								{#snippet loading()}
									{@render loader(4)}
								{/snippet}

								{#snippet children(row: any, i: number)}
									<tr
										class={cn(
											'border-b border-gray-200 last:border-0!',
											striped && i % 2 === 1 ? 'bg-gray-50' : '',
											hover ? 'hover:bg-gray-100' : '',
											'last:rounded-b-lg select-none',
											onRowClick ? 'cursor-pointer' : 'cursor-default'
										)}
										onclick={() => onRowClick?.(row, i)}
									>
										{#each columns as col}
											<td class={'px-3 py-2 ' + (col.class ?? '')}>
												{#if col.render}
													{@const rendered = col.render(row, i)}
													{#if typeof rendered === 'string'}
														{@html rendered}
													{:else}
														<rendered.component {...rendered.props} />
													{/if}
												{:else}
													{row[col.id]}
												{/if}
											</td>
										{/each}
									</tr>
								{/snippet}
							</ScrollPagination>
						{/key}
					{:else}
						{#each Array.isArray(rows) ? rows : rows.results as row, i}
							<tr
								class={cn(
									i !== (Array.isArray(rows) ? rows.length : rows.results.length) - 1
										? 'border-b border-gray-200'
										: '',
									striped && i % 2 === 1 ? 'bg-gray-50' : '',
									hover ? 'hover:bg-gray-100' : '',
									'last:rounded-b-lg select-none',
									onRowClick ? 'cursor-pointer' : 'cursor-default'
								)}
								onclick={() => onRowClick?.(row, i)}
							>
								{#each columns as col}
									<td class={'px-3 py-2 ' + (col.class ?? '')}>
										{#if col.render}
											{@const rendered = col.render(row, i)}
											{#if typeof rendered === 'string'}
												{@html rendered}
											{:else}
												<rendered.component {...rendered.props} />
											{/if}
										{:else}
											{row[col.id]}
										{/if}
									</td>
								{/each}
							</tr>
						{:else}
							<tr>
								<td
									class="px-3 py-1 text-center font-semibold text-lg text-secondaryText"
									colspan={columns.length}
								>
									{emptyMessage}
								</td>
							</tr>
						{/each}
					{/if}
				</tbody>
			</table>
		</div>
	</div>
</div>
