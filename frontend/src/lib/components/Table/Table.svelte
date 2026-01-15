<script lang="ts">
  import { cn } from "$lib/utils";
  import { m } from '$lib/paraglide/messages.js';
	import ScrollPagination from "$components/ScrollPagination/ScrollPagination.svelte";

  export interface Column<RowType> {
    id: keyof RowType | string;
    label: string;
    render?: (row: RowType) => any;
    class?: string;
  }

  interface Props<RowType> {
    rows: RowType[];
    columns: Column<RowType>[];
    striped?: boolean;
    bordered?: boolean;
    hover?: boolean;
    skeleton?: boolean;
    emptyMessage?: string;
    /**
     * URL source for pagination (auto-pagination wanted)
     */
    paginationSource?: string;
    onRowClick?: (row: RowType) => void;
    class?: string;
  }

  let {
    rows,
    columns,
    striped = false,
    bordered = false,
    hover = false,
    skeleton = false,
    emptyMessage = m.no_data_available({}),
    paginationSource,
    onRowClick,
    class: tableClass = ""
  }: Props<any> = $props();
</script>

{#snippet loader(count: number)}
  {#each Array(count) as _, i}
    <tr
      class={
        cn(
          i !== 4 ? "border-b border-gray-200" : "",
          "last:rounded-b-lg"
        )
      }>
      {#each columns as col}
        <td class={"px-3 py-2 " + (col.class ?? "")}>
          <div class="w-full h-4 bg-skeleton animate-pulse rounded-md"></div>
        </td>
      {/each}
    </tr>
  {/each}
{/snippet}

<div class={bordered && rows.length > 0 ? "shadow-lg rounded-lg" : ""}>
  <div class={tableClass}>
    <div class="h-full overflow-hidden">
      <div class="h-full overflow-y-auto scrollbar-thin scrollbar-thumb-table-header scrollbar-track-transparent">
        <table class="w-full table-fixed border-collapse">
          <thead class="bg-table-header text-white sticky top-0 z-10 cursor-default! select-none">
            <tr>
              {#each columns as col}
                <th class={"text-left px-3 py-2 font-semibold first:rounded-tl-lg last:rounded-tr-lg"}>
                  {col.label}
                </th>
              {/each}
            </tr>
          </thead>
    
          <tbody>
            {#if skeleton}
              {@render loader(Math.min(5, Math.max(2, rows.length)))}
            {:else if paginationSource}
              <ScrollPagination href={paginationSource}>
                {#snippet loading()}
                  {@render loader(4)}
                {/snippet}

                {#snippet children(row: any, i: number)}
                  <tr
                    class={cn(
                      "border-b border-gray-200 last:border-0!",
                      striped && i % 2 === 1 ? "bg-gray-50" : "",
                      hover ? "hover:bg-gray-100" : "",
                      "last:rounded-b-lg select-none",
                      onRowClick ? "cursor-pointer" : "cursor-default"
                    )}
                    onclick={() => onRowClick?.(row)}
                  >
                    {#each columns as col}
                      <td class={"px-3 py-2 " + (col.class ?? "")}>
                        {#if col.render}
                          {@const rendered = col.render(row)}
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
            {:else}
              {#each rows as row, i}
                <tr
                  class={cn(
                    i !== rows.length - 1 ? "border-b border-gray-200" : "",
                    striped && i % 2 === 1 ? "bg-gray-50" : "",
                    hover ? "hover:bg-gray-100" : "",
                    "last:rounded-b-lg select-none",
                    onRowClick ? "cursor-pointer" : "cursor-default"
                  )}
                  onclick={() => onRowClick?.(row)}
                >
                  {#each columns as col}
                    <td class={"px-3 py-2 " + (col.class ?? "")}>
                      {#if col.render}
                        {@const rendered = col.render(row)}
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
                  <td class="px-3 py-1 text-center font-semibold text-lg text-secondaryText" colspan={columns.length}>
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
</div>
