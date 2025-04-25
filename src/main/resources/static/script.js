function checkFiles(files) {
    console.log("Files selected:", files);

    if (files.length != 1) {
        alert("Please upload exactly one image file.");
        return;
    }

    const fileSize = files[0].size / 1024 / 1024; // in MiB
    if (fileSize > 10) {
        alert("File is too large (max 10MB)");
        return;
    }

    // Show preview
    const previewContainer = document.getElementById('previewContainer');
    previewContainer.style.display = 'block';
    const preview = document.getElementById('preview');
    
    if (files[0]) {
        preview.src = URL.createObjectURL(files[0]);
    }

    // Show loading state
    const resultContainer = document.getElementById('resultContainer');
    resultContainer.style.display = 'block';
    
    const loadingIndicator = document.getElementById('loadingIndicator');
    loadingIndicator.style.display = 'block';
    
    const resultsDiv = document.getElementById('results');
    resultsDiv.innerHTML = '';

    // Upload and analyze
    const formData = new FormData();
    formData.append("image", files[0]);

    fetch('/analyze', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        // Hide loading indicator
        loadingIndicator.style.display = 'none';
        
        // Display results
        displayResults(data);
    })
    .catch(error => {
        console.error('Error:', error);
        loadingIndicator.style.display = 'none';
        resultsDiv.innerHTML = `
            <div class="alert alert-danger">
                Error analyzing image: ${error.message}
            </div>
        `;
    });
}

function displayResults(data) {
    const resultsDiv = document.getElementById('results');
    
    // Check if data is in the expected format
    if (!data || !data.classNames || !data.probabilities) {
        resultsDiv.innerHTML = `
            <div class="alert alert-warning">
                Received unexpected data format from the server.
            </div>
        `;
        return;
    }
    
    // Find top prediction
    let topPrediction = {
        className: '',
        probability: 0
    };
    
    for (let i = 0; i < data.classNames.length; i++) {
        if (data.probabilities[i] > topPrediction.probability) {
            topPrediction.className = data.classNames[i];
            topPrediction.probability = data.probabilities[i];
        }
    }
    
    // Create result HTML
    let resultHtml = `
        <div class="mb-3">
            <h5>Detected Pattern: <span class="badge badge-primary">${topPrediction.className}</span></h5>
            <p>Confidence: ${(topPrediction.probability * 100).toFixed(2)}%</p>
        </div>
        <h6>All Patterns:</h6>
    `;
    
    // Add progress bars for all patterns
    for (let i = 0; i < data.classNames.length; i++) {
        const probability = data.probabilities[i];
        const percentage = (probability * 100).toFixed(2);
        const className = data.classNames[i];
        
        resultHtml += `
            <div class="pattern-item">
                <div class="d-flex justify-content-between">
                    <span>${className}</span>
                    <span>${percentage}%</span>
                </div>
                <div class="progress">
                    <div class="progress-bar ${className === topPrediction.className ? 'bg-success' : ''}" 
                         role="progressbar" 
                         style="width: ${percentage}%" 
                         aria-valuenow="${percentage}" 
                         aria-valuemin="0" 
                         aria-valuemax="100"></div>
                </div>
            </div>
        `;
    }
    
    resultsDiv.innerHTML = resultHtml;
}