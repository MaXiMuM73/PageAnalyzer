package controller;

import model.Page;
import org.apache.log4j.Logger;
import service.PageService;
import service.impl.PageServiceImpl;

import java.util.*;

public class Controller {
    private static final Logger logger = Logger.getLogger(Controller.class);
    private static final PageService service = new PageServiceImpl();
    private static final Scanner scanner = new Scanner(System.in);
    private static Page page;

    public static void main(String[] args) {
        logger.info("Page Analyzer started.");
        System.out.println("Welcome to Page Analyzer!");
        mainMenu();
        String userIn = "";
        while (!userIn.equals("8")) {
            userIn = scanner.nextLine();
            switch (userIn) {
                case "1":
                    variantOne();
                    break;
                case "2":
                    variantTwo();
                    break;
                case "3":
                    variantThree();
                    break;
                case "4":
                    variantFour();
                    break;
                case "5":
                    variantFive();
                    break;
                case "6":
                    variantSix();
                    break;
                case "7":
                    variantSeven();
                    break;
                case "8":
                    variantEight();
                    break;
                default:
                    System.out.println("Incorrect choice");
                    System.out.println("Please, choose the action:");
                    mainMenu();
                    break;
            }
        }
    }

    private static void mainMenu() {
        System.out.println("\nPlease, select an action:");
        System.out.println("1 - Download HTML page statistics and save the page to your hard drive" +
                "\n (storing the received data in RAM and save the page in the /pages folder)");
        System.out.println("2 - Show statistics in the console");
        System.out.println("3 - Save statistics to the database");
        System.out.println("4 - Display pages in database");
        System.out.println("5 - Display words in database");
        System.out.println("6 - Delete page from database by ID");
        System.out.println("7 - Download large HTML page statistics and save the page to your hard drive. \nYou can load a page of any size" +
                " (save the page and statistics to files in the /files folder)");
        System.out.println("8 - Exit");
        System.out.print("Your choice: ");
    }

    private static void variantOne() {
        System.out.println("Example: https://devcolibri.com/");
        System.out.print("Enter URL ->>> ");
        String url = scanner.nextLine();
        logger.info("Request URL - " + url + ".");
        page = service.getPage(url);
        //service.saveToHDD();
        mainMenu();
    }

    private static void variantTwo() {
        if (page!=null) {
            page.print();
            System.out.println("Count of words: " + page.getWords().size() + ".");
            mainMenu();
        } else {
            System.out.println("Statistics is empty. You need to load the page.");
            variantOne();
        }
    }

    private static void variantThree() {
        if (page!=null) {
            if (service.saveToDataBase(page)) {
                logger.info("Statistics saved to database.");
            } else {
                logger.error("Error occurred during the save statistics in database.");
            }
            mainMenu();
        } else {
            System.out.println("Statistics is empty. You need to load the page.");
            variantOne();
        }
    }

    private static void variantFour() {
        List<Page> allPagesList = service.findAll();
        for (Page p : allPagesList)
            System.out.println(p);
        mainMenu();
    }

    private static void variantFive() {
        List<String> words = service.findAllWords();
        for (String s : words)
            System.out.println(s);
        mainMenu();
    }

    private static void variantSix() {
        System.out.print("Enter ID ->>> ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            service.deleteById(id);
        } catch (NumberFormatException numberFormatException) {
            logger.error("Incorrect ID", numberFormatException);
        }
        mainMenu();
    }

    private static void variantSeven() {
        System.out.println("Example: https://devcolibri.com/");
        System.out.print("Enter URL ->>> ");
        String url = scanner.nextLine();
        logger.info("Request URL - " + url + ".");
        if (service.saveLargePageByUrl(url)) logger.info("Save completed. Check the /files folder.");
        mainMenu();
    }

    private static void variantEight() {
        logger.info("Page Analyzer closed.");
        System.exit(0);
    }
}