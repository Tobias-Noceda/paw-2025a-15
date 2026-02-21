import { apiOrigin, del, deleteAuth, getAuth, patchAuth, postAuth, resolveNonTemplatedLinks } from "$modules/api.svelte";
import type { Paginated, Study } from "$types/api";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";
import type { StudyType } from "$types/enums/studyTypes";
import { createFile } from "./files";

export const fetchStudies = async (url: string, studyType?: string, order?: string, fetchFn: typeof fetch = fetch): Promise<Paginated<Study>> => {
    const baseUrl = new URL(url);
    if (studyType && studyType !== "all") {
        baseUrl.searchParams.append('studyType', studyType);
    }
    if (order && order === 'l_recent') {
        baseUrl.searchParams.append('recent', "false");
    }
    baseUrl.searchParams.append('pageSize', '15');

    const response = await getAuth(baseUrl.toString(), undefined, fetchFn);

    if (response.ok) {
        const studiesData = await response.json();
        
        return {
            results: studiesData,
            _links: getPaginationLinks(response),
            _pageInfo: getPageInfoFromHeaders(response)
        };
    }

    throw error(response.status || 500, 'Failed to fetch studies');
};

export const fetchStudiesPage = async (url: string, fetchFn: typeof fetch = fetch): Promise<Paginated<Study>> => {
    const response = await getAuth(url, undefined, fetchFn);

    if (response.ok) {
        const studiesData = await response.json();
        
        return {
            results: studiesData,
            _links: getPaginationLinks(response),
            _pageInfo: getPageInfoFromHeaders(response)
        };
    }

    throw error(response.status || 500, 'Failed to fetch studies');
};

export const fetchSingleStudy = async (patientId: number, studyId: number, fetchFn: typeof fetch = fetch): Promise<Study> => {
    if (!patientId || !studyId) {
        throw error(404, 'Patient ID and Study ID are required');
    }
    const url = `${apiOrigin}/patients/${patientId}/studies/${studyId}`;
    const response = await getAuth(url, undefined, fetchFn);

    if (response.ok) {
        const study: Study = await response.json();

        resolveNonTemplatedLinks(study);

        if (study) {
            const filesResponse = await getAuth(study.links.files.resolved!, undefined, fetchFn)
                .catch(() => null);
            
            if (filesResponse && filesResponse.ok) {
                const filesData = await filesResponse.json();
                study.files = {
                    results: filesData,
                    _links: getPaginationLinks(filesResponse),
                    _pageInfo: getPageInfoFromHeaders(filesResponse)
                };
            }

            const uploader = await getAuth(study.links.uploader.resolved!, undefined, fetchFn).then(res => {
                if (res.ok) {
                    return res.json();
                }
                return null;
            }).catch(() => null);
            study.uploaderName = uploader?.name;
        }

        return study;
    }

    throw error(response.status || 500, 'Failed to fetch study');
};

export const authorizeDoctorsForStudy = async (studyPath: string, doctorSelfs: string[], fetchFn: typeof fetch = fetch): Promise<void> => {
    const body = {
        doctors: doctorSelfs,
        authorize: true
    };

    const response = await patchAuth(
        `${studyPath}`,
        body,
        {
            headers: {
                'Content-Type': 'application/vnd.patients.studies.v1+json'
            }
        },
        fetchFn
    );
    
    if (!response.ok) {
        throw error(response.status || 500, 'Failed to authorize doctors for study');
    }
};

export const unauthorizeDoctorsForStudy = async (studyPath: string, doctorSelfs: string[], fetchFn: typeof fetch = fetch): Promise<void> => {
    const body = {
        doctors: doctorSelfs,
        authorize: false
    };

    const response = await patchAuth(
        `${studyPath}`,
        body,
        {
            headers: {
                'Content-Type': 'application/vnd.patients.studies.v1+json'
            }
        },
        fetchFn
    );
    
    if (!response.ok) {
        throw error(response.status || 500, 'Failed to authorize doctors for study');
    }
};

export const createStudy = async (path: string, studyData: { comment: string; studyDate: Date; type: StudyType, authDoctors: string[], files: File[] }, fetchFn: typeof fetch = fetch): Promise<string> => {
    let filesLocations: string[] = [];
    for (const file of studyData.files) {
        filesLocations.push(await createFile(file, fetchFn));
    }

    const body = {
        comment: studyData.comment,
        studyDate: studyData.studyDate.toISOString(),
        type: studyData.type,
        authorizedDoctors: studyData.authDoctors,
        files: filesLocations
    };

    const response = await postAuth(
        `${path}`,
        body,
        {
            headers: {
                'Content-Type': 'application/vnd.patients.studies.v1+json'
            }
        },
        fetchFn
    );

    if (response.ok) {
        const location = response.headers.get('Location');
        if (location) {
            return location;
        } else {
            throw new Error('Study created but no Location header found');
        }
    }

    throw error(response.status || 500, 'Failed to create study');
};

export const deleteStudy = async (studyPath: string, fetchFn: typeof fetch = fetch): Promise<void> => {
    const response = await deleteAuth(studyPath, undefined, fetchFn);

    if (!response.ok) {
        throw error(response.status || 500, 'Failed to delete study');
    }
};
