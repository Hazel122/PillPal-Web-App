const form = document.getElementById('medicationForm');
const resultDiv = document.getElementById('result');

form.addEventListener('submit', async (e) => {
   e.preventDefault(); // Prevent form from refreshing the page

   const medicationsInput = document.getElementById('medications').value;
   const allergiesInput = document.getElementById('allergies').value;

   if (!medicationsInput) { // if no inpu
      resultDiv.innerHTML = '<p>Please enter at least one medication.</p>';
      return;
   }

   const medicationList = medicationsInput.split(',').map(med => med.trim().toUpperCase()); // convert to a list of medications
   const allergyList = allergiesInput ? allergiesInput.split(',').map(allergy => allergy.trim().toUpperCase()) : [];//convert to a list of allergies

   //get response and display results
   try {
      const activeIngredientsMap = await getActiveIngredients(medicationList);
      const issues = analyzeMedications(activeIngredientsMap, allergyList, medicationList);
      displayResults(issues);
   } catch (error) {
      resultDiv.innerHTML = `<p>Error: ${error.message}</p>`;
   }
});

// Function to get active ingredients from FDA API
async function getActiveIngredients(medicationList) {
    const activeIngredientsMap = {}; // initialize a map to get the active ingredients

    // go through each medication
    for (const medication of medicationList) {
        const query = `"${medication}"`;
        const url = `https://api.fda.gov/drug/label.json?search=${encodeURIComponent(query)}&limit=1`;

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Unable to fetch data for medication: ${medication}`);
        }

        const data = await response.json();
        if (data.results && data.results.length > 0) {
            const result = data.results[0];
            let ingredients = [];
            if (result.openfda && result.openfda.substance_name) {
                ingredients = result.openfda.substance_name.map(name => name.toUpperCase());
            } else if (result.active_ingredient) {
                ingredients = result.active_ingredient.map(name => name.toUpperCase());
            }

            activeIngredientsMap[medication] = ingredients;
        }
    }

    return activeIngredientsMap;
}
// Function to count how many medications are in the input
function getNumberOfMedications(medicationList) {
   count = 0;
   for (medication of medicationList) count += 1;
   return count;
}

// Function to analyze medications for issues
function analyzeMedications(activeIngredientsMap, allergyList,medicationList) {
    const issues = [];
    const allIngredients = [];
    const ingredientMedicationsMap = {};

    for (const [medication, ingredients] of Object.entries(activeIngredientsMap)) {
        if (activeIngredientsMap.length < getNumberOfMedications(medicationList) || ingredients.length === 0 ) {
            issues.push(`No active ingredients found for medication: ${medication}.`);
            continue;
        }

        // Check for allergies
        const allergyMatches = ingredients.filter(ingredient => allergyList.includes(ingredient));
        if (allergyMatches.length > 0) {
            issues.push(`Medication ${medication} contains ingredient(s) you're allergic to: ${allergyMatches.join(', ')}.`);
        }

        // Build a map of ingredients to medications
        for (const ingredient of ingredients) {
            allIngredients.push(ingredient);
            if (!ingredientMedicationsMap[ingredient]) {
                ingredientMedicationsMap[ingredient] = [];
            }
            ingredientMedicationsMap[ingredient].push(medication);
        }
    }

    // Check for duplicate ingredients in multiple medications
    const duplicateIngredients = Object.entries(ingredientMedicationsMap).filter(([ingredient, meds]) => meds.length > 1);
    for (const [ingredient, meds] of duplicateIngredients) {
        issues.push(`Ingredient ${ingredient} is present in multiple medications: ${meds.join(', ')}.`);
    }

    // Note: Checking for interactions between different active ingredients is complex and may require specialized databases.

    return issues;
}

// Function to display results
function displayResults(issues) {
    resultDiv.innerHTML = '';

    if (issues.length === 0) {
        resultDiv.innerHTML = '<p>No issues found with the entered medications and allergies.</p>';
    } else {
        resultDiv.innerHTML = '<h3>Possible Issues Found:</h3>';
        const ul = document.createElement('ul');
        issues.forEach(issue => {
            const li = document.createElement('li');
            li.textContent = issue;
            ul.appendChild(li);
        });
        resultDiv.appendChild(ul);
    }
}
