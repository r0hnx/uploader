"use client";
import { useFolderContents } from "@/lib/api";
import { Folder, FileText, Loader2 } from "lucide-react";
import Link from "next/link";
import { use } from "react";

export default function FolderPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const { data, error } = useFolderContents(id);

  if (error) return <p className="text-red-500 text-center">Oops! Something went wrong.</p>;
  if (!data) {
    return (
      <div className="flex justify-center items-center h-[80vh]">
        <Loader2 className="w-10 h-10 text-blue-500 animate-spin" />
      </div>
    );
  }

  if(data.error) {
    return (
      <div className="flex justify-center items-center h-[80vh]">
        <h1>Error 404 | Not Found</h1>
      </div>
    )
  }

  const hasFolders = data.folders && data.folders.length > 0;
  const hasFiles = data.files && data.files.length > 0;

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">{data.name}</h2>

      {(hasFolders || hasFiles) && (
        <div className="grid grid-cols-3 gap-4">
          {data.folders.map((folder: { id: string; name: string }) => (
            <Link key={folder.id} href={`/folder/${folder.id}`}>
              <div className="p-4 bg-gray-100 rounded-lg flex items-center gap-2 cursor-pointer hover:bg-gray-200 transition">
                <Folder className="w-6 h-6" />
                {folder.name}
              </div>
            </Link>
          ))}

          {data.files.map((file: { id: string; filename: string }) => (
            <div key={file.id} className="p-4 bg-white shadow rounded-lg flex items-center gap-2">
              <FileText className="w-6 h-6" />
              {file.filename}
            </div>
          ))}
        </div>
      )}

      {!hasFolders && !hasFiles && (
        <div className="flex flex-col justify-center items-center h-[80vh] text-gray-500">
          <Folder className="w-16 h-16 opacity-50" />
          <p className="mt-4 text-lg">So empty here... even the dust bunnies left. 🫠</p>
        </div>
      )}
    </div>
  );
}
