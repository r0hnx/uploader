# File Manager App

This is a **full-stack file manager application** built with **Next.js (frontend)** and **Spring Boot (backend)** using **SQLite** as the database.

## Features
- 📂 **Upload and manage files**
- 🗂 **Create folders**
- 📥 **Download files**
- 🔄 **Auto-refresh UI on updates**
- 🚀 **Minimal, smooth, and lightweight UI with TailwindCSS & ShadCN**
- 🛠 **Backend built with Java 21, Spring Boot, and SQLite**

## Tech Stack
### Frontend
- **Next.js**
- **React (ShadCN, SWR, TailwindCSS)**
- **TypeScript**

### Backend
- **Java 21**
- **Spring Boot**
- **SQLite Database**
- **Hibernate ORM**

## Installation
### Prerequisites
- **Node.js (v18+)**
- **Java 21**
- **Maven**
- **Docker (Optional for deployment)**

### Backend Setup
1. Navigate to the backend folder:
   ```sh
   cd backend
   ```
2. Build and run the Spring Boot app:
   ```sh
   mvn spring-boot:run
   ```
3. The API should now be running at `http://localhost:8080`

### Frontend Setup
1. Navigate to the frontend folder:
   ```sh
   cd frontend
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Run the development server:
   ```sh
   npm run dev
   ```
4. Open `http://localhost:3000` in your browser.

## API Endpoints
| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET`  | `/api/root` | Fetch root-level files & folders |
| `POST` | `/api/upload` | Upload a file |
| `POST` | `/api/folders` | Create a new folder |
| `GET`  | `/api/download?path=<file-path>` | Download a file |

## Deployment
For **Docker** deployment, ensure Docker is installed, then run:
```sh
docker-compose up --build
```

For **Vercel**, run:
```sh
vercel --prod
```

## Future Improvements
- 👤 **User authentication & permissions**
- 📁 **Folder hierarchy navigation**
- 🔍 **File search functionality**

## License
MIT License. Feel free to use and modify!

