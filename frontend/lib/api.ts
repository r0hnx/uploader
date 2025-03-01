import useSWR from "swr";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

const fetcher = (url: string) => fetch(`${API_BASE_URL}${url}`).then(res => res.json());

interface File {
  id: string;
  filename: string;
}

interface Folder {
  id: string;
  name: string;
}

interface ApiResponse {
  name: string,
  folders: Folder[];
  files: File[];
}

export function useFolders() {
  return useSWR<ApiResponse>("/api/folders", fetcher);
}

export function useFolderContents(folderId: string) {
  return useSWR<ApiResponse>(`/api/folders/${folderId}`, fetcher);
}