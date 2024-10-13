import java.util.*;

// Main class for managing users and providing menu options
public class AllergyMedicationTracker {
    private static final String[] VALID_UNITS = {"hour", "day", "week", "month", "year"};
    private static HashMap<String, User> userDatabase = new HashMap<>(); // Simulates a database of users

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to PillPal!");
            System.out.println("1. Log In");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                logIn(scanner);
            } else if (choice.equals("2")) {
                register(scanner);
            } else if (choice.equals("3")) {
                System.out.println("Thank you for choosing PillPal!");
                running = false;
            } else {
                System.out.println("Invalid option. Please choose again.");
            }
        }

        scanner.close();
    }

    // User login process
    private static void logIn(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (userDatabase.containsKey(name) && userDatabase.get(name).authenticate(password)) {
            System.out.println("Login successful!");
            User user = userDatabase.get(name);
            showUserMenu(scanner, user);
        } else {
            System.out.println("Invalid name or password.");
        }
    }

    // User registration process
    private static void register(Scanner scanner) {
        System.out.print("Enter a new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter a new password: ");
        String password = scanner.nextLine();

        if (userDatabase.containsKey(name)) {
            System.out.println("This name is already registered.");
        } else {
            User newUser = new User(name, password);
            userDatabase.put(name, newUser);
            System.out.println("Registration successful!");
            // Automatically log the user in after registration
            showUserMenu(scanner, newUser);
        }
    }

    // Menu options for managing a user's allergies, medications, and symptoms
    private static void showUserMenu(Scanner scanner, User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Allergy");
            System.out.println("2. Remove Allergy");
            System.out.println("3. View Allergies");
            System.out.println("4. Add Medication");
            System.out.println("5. Remove Medication");
            System.out.println("6. View Medications");
            System.out.println("7. Log Symptoms");
            System.out.println("8. View Symptoms Log");
            System.out.println("9. Log Out");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine();

            if (option.equals("1")) {
                addAllergy(scanner, user);
            } else if (option.equals("2")) {
                removeAllergy(scanner, user);
            } else if (option.equals("3")) {
                user.displayAllergies();
            } else if (option.equals("4")) {
                addMedication(scanner, user);
            } else if (option.equals("5")) {
                removeMedication(scanner, user);
            } else if (option.equals("6")) {
                user.displayMedications();
            } else if (option.equals("7")) {
                logSymptoms(scanner, user);
            } else if (option.equals("8")) {
                user.displaySymptomsLog();
            } else if (option.equals("9")) {
                System.out.println("Logging out...");
                loggedIn = false;
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Add an allergy for a user
    private static void addAllergy(Scanner scanner, User user) {
        System.out.print("Enter the allergy: ");
        String allergy = scanner.nextLine();
        user.addAllergy(allergy);
    }

    // Remove an allergy for a user
    private static void removeAllergy(Scanner scanner, User user) {
        System.out.print("Enter the allergy to remove: ");
        String allergy = scanner.nextLine();
        user.removeAllergy(allergy);
    }

    // Add a medication for a user
    private static void addMedication(Scanner scanner, User user) {
        System.out.print("Enter medication name: ");
        String medName = scanner.nextLine();
        System.out.print("Enter dosage (number of tablets/pills): ");
        int dosage = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        String frequency = getValidatedFrequency(scanner);
        Medication medication = new Medication(medName, dosage, frequency);
        user.addMedication(medication);
    }

    // Remove a medication for a user
    private static void removeMedication(Scanner scanner, User user) {
        System.out.print("Enter the name of the medication to remove: ");
        String medName = scanner.nextLine();
        user.removeMedication(medName);
    }

    // Log symptoms for a user
    private static void logSymptoms(Scanner scanner, User user) {
        String date = getValidatedDate(scanner);  // Get a valid date
        System.out.print("Enter your symptoms: ");
        String symptoms = scanner.nextLine();
        user.addSymptoms(date, symptoms);
        System.out.println("Symptoms logged successfully.");
    }

    // Get and validate the medication frequency
    private static String getValidatedFrequency(Scanner scanner) {
        System.out.println("Enter how often the medication is taken.");
        System.out.println("Options for units are: hour, day, week, month, or year.");

        String unit = "";
        boolean validUnit = false;

        // Validate unit input
        while (!validUnit) {
            System.out.print("Enter the unit (hour/day/week/month/year): ");
            unit = scanner.nextLine().toLowerCase();
            for (String valid : VALID_UNITS) {
                if (unit.equals(valid)) {
                    validUnit = true;
                    break;
                }
            }
            if (!validUnit) {
                System.out.println("Invalid unit! Please choose from hour, day, week, month, or year.");
            }
        }

        int timesPerUnit = 0;
        boolean validTimes = false;

        // Validate number of times input
        while (!validTimes) {
            System.out.print("Enter how many times per " + unit + ": ");
            if (scanner.hasNextInt()) {
                timesPerUnit = scanner.nextInt();
                scanner.nextLine(); // Consume the newline
                validTimes = true;
            } else {
                System.out.println("Please enter a valid number for how many times per " + unit + ".");
                scanner.nextLine(); // Consume the invalid input
            }
        }

        return timesPerUnit + " times per " + unit;
    }

    // Get and validate the date in month/day/year format
    private static String getValidatedDate(Scanner scanner) {
        String date = "";
        boolean validDate = false;

        // Continue to prompt until a valid date format is entered
        while (!validDate) {
            System.out.print("Enter today's date (MM/DD/YYYY): ");
            date = scanner.nextLine();
            if (date.matches("\\d{2}/\\d{2}/\\d{4}")) {
                validDate = true;
            } else {
                System.out.println("Invalid date format. Please use MM/DD/YYYY.");
            }
        }

        return date;
    }
}