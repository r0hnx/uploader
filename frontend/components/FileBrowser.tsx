"use client";
import { Folder, FileText, Loader2, Upload, Plus, MoreVertical } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import Link from "next/link";
import { useState } from "react";
import useSWR from "swr";
import { Button } from "@/components/ui/button";
import { Dialog, DialogTrigger, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem } from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";

const fetcher = (url: string): Promise<ApiResponse> =>
  fetch(url).then((res) => res.json());

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

export default function FileBrowser({ folderId }: { folderId?: string }) {
  const apiUrl = folderId ? `http://localhost:8080/api/folders/${folderId}` : "http://localhost:8080/api/folders";
  const { data, error, isLoading, mutate } = useSWR<ApiResponse>(
    apiUrl,
    fetcher
  );
  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [creatingFolder, setCreatingFolder] = useState(false);
  const [folderName, setFolderName] = useState("");
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const handleUploadClick = () => {
    document.getElementById("file-upload")?.click();
  };

  const handleCreateFolder = async () => {
    if (!folderName) return;

    try {
      setCreatingFolder(true);
      await fetch("http://localhost:8080/api/folders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: folderName, parentId: folderId || null }),
      });
      mutate();
      setFolderName("");
      setIsDialogOpen(false);
    } finally {
      setCreatingFolder(false);
    }
  };

  const handleDownload = (id: string, fileName: string) => {
    const link = document.createElement("a");
    link.href = `http://localhost:8080/api/download?path=${encodeURIComponent(id)}`;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    if (!event.target.files || event.target.files.length === 0) {
      setUploadError("No file selected.");
      return;
    }

    const file = event.target.files[0];
    const formData = new FormData();
    formData.append("file", file);

    try {
      setUploading(true);
      setUploadError(null);

      const response = await fetch("http://localhost:8080/api/upload", {
        method: "POST",
        body: formData,
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || "Upload failed.");
      }

      mutate();
    } catch (error) {
      setUploadError(`Upload error: ${error instanceof Error ? error.message : "Unknown error"}`);
      console.error("Upload error:", error);
    } finally {
      setUploading(false);
      event.target.value = "";
    }
  };

  return (
    <div className="flex flex-col h-screen p-6">
      {/* Upload & Create Folder Buttons */}
      <div className="flex gap-4 mb-6">
        <label htmlFor="file-upload">
          <input type="file" id="file-upload" className="hidden" onChange={handleUpload} />
          <Button variant="default" className="py-3 flex gap-2" onClick={handleUploadClick}>
            <Upload size={24} />
            {uploading ? "Uploading..." : "Upload File"}
          </Button>
        </label>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button variant="default" className="py-3 flex gap-2">
              <Plus size={24} /> Create Folder
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Create New Folder</DialogTitle>
            </DialogHeader>
            <Input placeholder="Folder Name" value={folderName} onChange={(e) => setFolderName(e.target.value)} />
            <DialogFooter>
              <Button onClick={handleCreateFolder} disabled={creatingFolder}>
                {creatingFolder ? "Creating..." : "Create"}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>

      {/* Show Upload Errors */}
      {uploadError && <p className="text-red-500 mt-2 text-sm">{uploadError}</p>}
      <h2 className="text-2xl font-semibold my-4">File Explorer</h2>

      {/* Loading State */}
      {isLoading && (
        <div className="flex justify-center items-center h-full">
          <Loader2 size={32} className="animate-spin" />
        </div>
      )}

      {/* Error State */}
      {error && <p className="text-red-500">Failed to load data.</p>}

      {/* Empty State */}
      {!isLoading && data?.folders?.length === 0 && data?.files?.length === 0 && (
        <div className="flex flex-col items-center justify-center h-full">
          <p className="text-gray-400 text-lg">So empty here... 📂</p>
        </div>
      )}

      {/* Files & Folders Grid */}
      <div className="grid grid-cols-3 gap-4">
        {data?.folders?.map((folder: Folder) => (
          <Link key={folder.id} href={`/folder/${folder.id}`}>
            <Card key={folder.id} className="p-4">
              <CardContent className="flex flex-col items-center">
                <Folder size={40} />
                <p className="mt-2">{folder.name}</p>
              </CardContent>
            </Card>
          </Link>
        ))}

        {data?.files?.map((file) => (
          <Card key={file.id} className="p-4">
            <CardContent className="flex flex-col items-center relative">
              <FileText size={40} />
              <p className="mt-2">{file.filename}</p>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="absolute top-2 right-2">
                    <MoreVertical size={18} />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent>
                  <DropdownMenuItem onClick={() => handleDownload(file.id, file.filename)}>
                    Download
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
