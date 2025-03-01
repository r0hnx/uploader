"use client";
import { useState } from "react";

export function UploadButton() {
  const [file, setFile] = useState<File | null>(null);

  const handleUpload = async () => {
    if (!file) return;
    const formData = new FormData();
    formData.append("file", file);

    await fetch("/api/files/upload", {
      method: "POST",
      body: formData,
    });

    setFile(null);
  };

  return (
    <div className="fixed bottom-6 right-6">
      <input
        type="file"
        onChange={(e) => setFile(e.target.files?.[0] || null)}
        className="hidden"
      />
      <button
        className="p-4 bg-blue-500 text-white rounded-full shadow-lg"
        onClick={handleUpload}
      >
        Upload
      </button>
    </div>
  );
}
