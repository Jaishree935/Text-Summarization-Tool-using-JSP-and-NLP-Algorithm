<%-- 
    Document   : index
    Created on : 1 Oct, 2024, 7:57:47 PM
    Author     : USER
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Text Summarizer</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background: linear-gradient(135deg, #b3e0ff, #ffcccb); /* Soft gradient */
            position: relative;
            overflow: hidden; /* Prevents overflow of shapes */
        }

        .container {
            background-color: rgba(255, 255, 255, 0.9); /* Slight transparency */
            border-radius: 15px;
            box-shadow: 0 0 30px rgba(0, 0, 0, 0.3);
            padding: 30px 50px;
            width: 600px; /* Increased width for better layout */
            position: relative;
            z-index: 1; /* Bring the container above the background shapes */
        }

        /* Adding decorative shapes */
        .shape {
            position: absolute;
            border-radius: 50%;
            opacity: 0.15; /* Transparency for the shapes */
        }

        .shape1 {
            width: 300px;
            height: 300px;
            background: #f0f8ff;
            top: -50px;
            left: -50px;
        }

        .shape2 {
            width: 400px;
            height: 400px;
            background: #ffebcd;
            bottom: -100px;
            right: -100px;
        }

        h2 {
            text-align: center;
            color: #333;
        }

        form {
            display: flex;
            flex-direction: column;
        }

/* Other existing styles... */


        label {
            margin: 10px 0 5px;
            font-weight: bold;
            color: #555;
        }

        select, textarea, input[type="file"] {
            width: 100%;
            padding: 12px;
            border-radius: 10px; /* Increased border radius */
            border: 1px solid #ddd;
            font-size: 14px;
            margin-bottom: 15px;
            transition: border-color 0.3s;
        }

        select:focus, textarea:focus, input[type="file"]:focus {
            border-color: #4CAF50;
            outline: none;
        }

        textarea {
            resize: none;
            height: 180px; /* Increased height for textarea */
            font-family: 'Arial', sans-serif;
        }

        input[type="submit"], .clear-btn {
            background-color: #4CAF50;
            color: white;
            padding: 12px 15px;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            font-size: 18px; /* Increased font size */
            transition: background-color 0.3s, transform 0.2s;
            margin: 5px 0; /* Spacing between buttons */
        }

        input[type="submit"]:hover, .clear-btn:hover {
            background-color: #45a049;
            transform: translateY(-2px);
        }

        .input-group {
            margin-bottom: 25px; /* Increased spacing */
        }

        .optional {
            color: #777;
            font-size: 12px;
            font-style: italic;
            margin-top: 5px; /* Add some space above optional note */
        }

        .button-group {
            display: flex;
            justify-content: space-between; /* Space between buttons */
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>JSP Smart Summarizer</h2>
        <form action="SummarizeServlet" method="post">
            <div class="input-group">
                <label for="inputType">Select Input Type:</label>
                <select id="inputType" name="inputType" required>
                    <option value="text">Manual Text</option>
                    <option value="pdf">PDF File</option>
                    <option value="txt">Text File</option>
                </select>
            </div>

            <div class="input-group">
                <label for="inputText">Manual Text:</label>
                <textarea id="inputText" name="inputText" placeholder="Enter text here..."></textarea>
            </div>

            <div class="input-group">
                <label for="filePath">PDF/Text File (Optional):</label>
                <input id="filePath" type="text" name="filePath" placeholder="Enter file path here" /><br>
                <span class="optional">* Only required if PDF or Text File is selected</span>
            </div>

            <div class="button-group">
                <input type="submit" value="Summarize">
                <input type="button" class="clear-btn" value="Clear" onclick="clearText()">
            </div>
        </form>
    </div>

    <script>
        function clearText() {
            document.getElementById("inputText").value = ""; // Clear the text area
            document.getElementById("filePath").value = "";  // Clear the file path
            document.getElementById("inputType").selectedIndex = 0; // Reset the dropdown
        }
    </script>
</body>
</html>