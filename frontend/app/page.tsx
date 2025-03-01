"use client";
import { useState } from "react";
import { Folder, FileText, Home, Settings, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { useFolders } from "@/lib/api";
import Link from "next/link";

export default function FileBrowser() {
  const { data, error, isLoading } = useFolders();

  return (
    <div className="flex h-screen">

      {/* Main Content */}
      <main className="flex-1 p-6">
        <h2 className="text-2xl font-semibold mb-4">Your Files</h2>

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
          {data?.folders?.map((folder: any) => (
            <Link key={folder.id} href={`/folder/${folder.id}`}>
              <Card key={folder.id} className="p-4">
                <CardContent className="flex flex-col items-center">
                  <Folder size={40} />
                  <p className="mt-2">{folder.name}</p>
                </CardContent>
              </Card>
            </Link>
          ))}

          {data?.files?.map((file: any) => (
            <Card key={file.id} className="p-4">
              <CardContent className="flex flex-col items-center">
                <FileText size={40} />
                <p className="mt-2">{file.name}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>
    </div>
  );
}
