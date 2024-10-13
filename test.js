document.getElementById("queryForm").addEventListener('submit', async (event) => {
   event.preventDefault(); // Prevents form submission
 
   const userQuery = document.getElementById("userQuery").value;
 
   const options = {
     method: 'POST',
     headers: {
       Authorization: 'Bearer api_token', // Ensure this token is correct
       'Content-Type': 'application/json'
     },
     body: JSON.stringify({
       model: "llama-3.1-sonar-small-128k-online",
       messages: [
         { role: "system", content: "Be precise and concise." },
         { role: "user", content: userQuery }
       ],
       max_tokens: Option,
       temperature: 0.2,
       top_p: 0.9,
       return_citations: true,
       search_domain_filter: ["perplexity.ai"],
       return_images: false,
       return_related_questions: true,
       search_recency_filter: "month",
       top_k: 0,
       stream: false,
       presence_penalty: 0,
       frequency_penalty: 1
     })
   };
 
   try {
     console.log('Sending request:', options);
     
     const response = await fetch('https://api.perplexity.ai/chat/completions', options);
     if (!response.ok) {
       throw new Error(`HTTP error! Status: ${response.status}`);
     }
 
     const data = await response.json();
     console.log('Data received:', data);
 
     const content = data.choices[0]?.message?.content; // Extracting the "content" part
     if (content) {
         const htmlContent = marked.parse(content); // Convert markdown to HTML
         document.getElementById('responseContainer').innerHTML = htmlContent; // Display markdown as HTML
      } else {
         document.getElementById('responseContainer').innerText = "Content not available";
      }
   } catch (error) {
     console.error('Fetch error:', error);
     document.getElementById('responseContainer').innerText =
       'Error: ' + error.message;
   }
 });
 
