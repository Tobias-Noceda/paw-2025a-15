import type { Paginated } from "$types/api";

export const getPaginationLinks = (response: Response) => {
    let links: Paginated<unknown>['_links'] = {};
    response.headers.get('Link')?.split(',').forEach(link => {
        const match = link.match(/<([^>]+)>;\s*rel="([^"]+)"/);
        if (match) {
            const [, linkUrl, rel] = match;
            links[rel as keyof typeof links] = linkUrl;
        }
    });

    return links;
};

const pageInfoKeys: Map<string, string> = new Map([
    ['X-Current-Page', 'currentPage'],
    ['X-Total-Pages', 'totalPages'],
    ['X-Current-Date', 'currentDate'],
    ['X-Max-Date', 'maxDate']
]);

export const getPageInfoFromHeaders = (response: Response) => {
    let pageInfo: Paginated<unknown>['_pageInfo'] = {};
    pageInfoKeys.forEach((value, key) => {
        if (response.headers.get(key)) {
            pageInfo = {
                ...pageInfo,
                [value]: Number(response.headers.get(key)),
            };
        }
    });

    return pageInfo;
};
