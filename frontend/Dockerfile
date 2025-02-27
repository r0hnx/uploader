# Use official Node.js image
FROM node:18-alpine-slim

# Set working directory
WORKDIR /app

# Copy package.json and install dependencies
COPY package.json yarn.lock ./
RUN yarn install

# Copy the rest of the application
COPY . .

# Build the frontend
RUN yarn build

# Expose port 3000
EXPOSE 3000

# Start Next.js app
CMD ["yarn", "start"]