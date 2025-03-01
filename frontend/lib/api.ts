import useSWR from "swr";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

const fetcher = (url: string) => fetch(`${API_BASE_URL}${url}`).then(res => res.json());

export function useFolders() {
  return useSWR("/api/folders", fetcher);
}

export function useFolderContents(folderId: string) {
  return useSWR(`/api/folders/${folderId}`, fetcher);
}