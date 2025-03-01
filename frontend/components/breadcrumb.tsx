"use client";
import useSWR from "swr";
import Link from "next/link";

export function Breadcrumb({ folderId }: { folderId: string }) {
  const { data } = useSWR(`/api/folders/${folderId}/breadcrumb`, (url) =>
    fetch(url).then((res) => res.json())
  );

  if (!data) return <p>Loading...</p>;

  return (
    <nav className="text-sm mb-4">
      {data.map((item: { id: string; name: string }) => (
        <span key={item.id}>
          <Link href={`/folder/${item.id}`} className="text-blue-500">
            {item.name}
          </Link>{" "}
          /{" "}
        </span>
      ))}
    </nav>
  );
}
