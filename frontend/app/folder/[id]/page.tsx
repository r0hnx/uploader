"use client";
import FileBrowser from "@/components/FileBrowser";
import { use } from "react";

export default function FolderPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);

  return <FileBrowser folderId={id} />;
}
