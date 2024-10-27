<%-- 
    Document   : summary
    Created on : 1 Oct, 2024, 7:58:38 PM
    Author     : USER
--%>

<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Text Summary</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background: linear-gradient(135deg, #e0f7fa, #ffccbc);
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            overflow: hidden;
        }

        h2 {
            color: #2c3e50;
            text-align: center;
            font-size: 28px;
            text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
            margin-bottom: 10px; /* Reduced space between h2 and container */
        }

        .container {
            width: 85%; /* Adjusted width to reduce excess space */
            max-width: 600px;
            background-color: rgba(255, 255, 255, 0.95);
            box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
            padding: 40px; /* Reduced padding for more compact look */
            border-radius: 12px;
            max-height: 70vh; /* Limited container height */
            overflow-y: hidden; /* Scroll only on vertical overflow */
            overflow-x: hidden; /* No horizontal scroll bar */
        }

        p {
            font-size: 16px;
            color: #333;
            line-height: 1.5;
            text-align: justify;
            padding: 10px;
            background-color: #fafafa;
            border-radius: 8px;
            border: 1px solid #ddd;
            margin: 10px 0; /* Reduced margin between paragraphs */
            width: 100%;
            box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
        }

        a {
            display: inline-block;
            text-decoration: none;
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px; /* Adjusted padding */
            border-radius: 5px;
            text-align: center;
            margin-top: 10px;
            transition: background-color 0.3s, transform 0.2s;
            font-size: 14px;
        }

        a:hover {
            background-color: #45a049;
            transform: translateY(-2px);
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 10px;
            width: 100%;
        }

    </style>
</head>
<body>

     <div class="container">
        <h2>Text InShort</h2>
        <p>${sessionScope.summary}</p>
         <p><strong>Original Text Word Count:</strong> ${sessionScope.originalWordCount}</p>
        <p><strong>Summarized Text Word Count:</strong> ${sessionScope.summarizedWordCount}</p>
        <div class="back-link">
            <a href="DownloadServlet?summary=${sessionScope.summary}" target="_blank">Download as PDF</a>
            <div class="back-link" style="margin-top: 10px;">
                <a href="index.jsp" class="back-button">Go Back</a>
            </div>
        </div>
    </div>

</body>
</html>
