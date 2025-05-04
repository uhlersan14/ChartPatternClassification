# Crypto Chart Pattern Classification

This project implements a web application for identifying technical analysis patterns from cryptocurrency chart images. It is developed as part of the **Java / Spring Boot Web** module using the **Deep Java Library (DJL)** for machine learning inference.

## 📊 Supported Chart Patterns

The model can classify uploaded chart images into the following categories:

- Ascending Triangle
- Descending Triangle
- Symmetrical Triangle
- Rising Wedge
- Falling Wedge
- Double Top
- Double Bottom

## 🔧 Technologies Used

- **Backend:** Java, Spring Boot Web
- **Machine Learning:** Deep Java Library (DJL)
- **Frontend:** HTML, CSS, JavaScript (Vanilla JS)

## 🚀 Approach

### 🧠 Model & Data

- A pre-trained model was used.
- The model is loaded locally when the application starts. No external services or scraping are required.
- If the model or dataset were to be trained, it would use a prepared dataset following DJL standards.

### 🕹 Web Application Flow

1. User uploads a chart image (JPEG or PNG).
2. The Spring Boot backend receives the image and uses DJL to classify the chart pattern.
3. The prediction results (class name and probability) are returned as JSON.
4. The frontend displays the results, highlighting the most probable pattern and showing progress bars for all predictions.

### 🖼 UI & Deployment

- A simple and user-friendly web interface was developed to upload images and display results.
- For evaluation purposes, deployment is not strictly required. Evidence (logs, screenshots, screencast) will be provided in the project report.
- Logs from the backend and screenshots of the UI are available as proof of functionality.

## 🗂 Project Structure