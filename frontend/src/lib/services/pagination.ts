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
