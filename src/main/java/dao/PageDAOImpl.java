package dao;

import exception.PageNotFoundException;
import model.Page;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageDAOImpl implements PageDAO {
    private static final Logger logger = Logger.getLogger(PageDAOImpl.class);
    private final String dbUrl;
    private static final String SQL_SELECT_ALL_PAGES = "SELECT * FROM pages";
    private static final String SQL_SELECT_ALL_WORDS = "SELECT * FROM words";
    private static final String SQL_SELECT_PAGE_BY_ID = "SELECT * FROM pages WHERE id = ?";
    private static final String SQL_SELECT_PAGE_BY_URL = "SELECT * FROM pages WHERE url = ?";
    private static final String SQL_INSERT_PAGE = "INSERT INTO pages (pageName, url) VALUES (?,?)";
    private static final String SQL_INSERT_WORD = "INSERT INTO words (word,count,id_page) VALUES (?,?,?)";
    private static final String SQL_DELETE_PAGE = "DELETE FROM pages WHERE id = ?";
    private static Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;

    public PageDAOImpl(String dbUrl) {
        this.dbUrl = dbUrl;
        getConnection();
        createTables();
        closeConnection();
    }

    private void getConnection() {
        try {
            Class.forName("org.h2.Driver");
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(dbUrl);
            }
        } catch (SQLException | ClassNotFoundException sqlException) {
            logger.error("Connection to database error", sqlException);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqlException) {
            logger.error("Close connection error.", sqlException);
        }
    }

    public void createTables() {
        String SQL_CREATE_TABLE_PAGES = "CREATE TABLE IF NOT EXISTS pages(" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "pageName VARCHAR(255) NOT NULL," +
                "url VARCHAR (255) NOT NULL)";
        String SQL_CREATE_TABLE_WORDS = "CREATE TABLE IF NOT EXISTS words(" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "word VARCHAR(255) NOT NULL," +
                "count INTEGER NOT NULL," +
                "id_page BIGINT NOT NULL)";
        String SQL_FK = "ALTER TABLE words ADD FOREIGN KEY (id_page) REFERENCES pages (id)" +
                " ON DELETE CASCADE";
        try {
            getConnection();
            Statement statement = connection.createStatement();
            statement.execute(SQL_CREATE_TABLE_PAGES);
            statement.execute(SQL_CREATE_TABLE_WORDS);
            statement.execute(SQL_FK);
            statement.close();
        } catch (SQLException sqlException) {
            logger.error("Error creating tables.", sqlException);
        } finally {
            closeConnection();
        }
    }

    @Override
    public List<Page> findAll() {
        List<Page> pageList = new ArrayList<>();
        try {
            getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_PAGES);
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String pageName = resultSet.getString(2);
                String url = resultSet.getString(3);
                pageList.add(new Page(id, pageName, url));
            }
            statement.close();
        } catch (SQLException sqlException) {
            logger.error("Error finding pages in the database.", sqlException);
        } finally {
            closeConnection();
        }
        return pageList;
    }

    @Override
    public List<String> findAllWords() {
        List<String> words = new ArrayList<>();
        try {
            getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_WORDS);
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String word = resultSet.getString(2);
                int count = resultSet.getInt(3);
                long pageId = resultSet.getLong(4);
                String wordWithCount = "ID: " + id + " Word: " + word + " Count: " + count + " PageId: " + pageId;
                words.add(wordWithCount);
            }
            statement.close();
        } catch (SQLException sqlException) {
            logger.error("Error finding words in the database", sqlException);
        } finally {
            closeConnection();
        }
        return words;
    }

    @Override
    public Page findById(Long id) {
        Page page = null;
        try {
            getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_PAGE_BY_ID);
            preparedStatement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String pageName = resultSet.getString(2);
                String url = resultSet.getString(3);
                page = new Page(id, pageName, url);
            } else {
                throw new PageNotFoundException(id);
            }
            preparedStatement.close();
        } catch (SQLException sqlException) {
            logger.error("Find by ID: " + id + " error.", sqlException);
        } catch (PageNotFoundException pageNotFoundException) {
            logger.info("Page with id " + id + " not found.");
        } finally {
            closeConnection();
        }
        return page;
    }

    @Override
    public Page findByUrl(String url) {
        Page page = null;
        try {
            getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_PAGE_BY_URL);
            preparedStatement.setString(1, url);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String pageName = resultSet.getString(2);
                page = new Page(id, pageName, url);
            } else {
                throw new PageNotFoundException(url);
            }
            preparedStatement.close();
        } catch (SQLException sqlException) {
            logger.error("Error finding the page by URL.", sqlException);
        } finally {
            closeConnection();
        }
        return page;
    }

    @Override
    public boolean create(Page page) {
        if (page == null | connection == null) return false;
        try {
            getConnection();
            preparedStatement = connection.prepareStatement(SQL_INSERT_PAGE, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, page.getPageName());
            preparedStatement.setString(2, page.getUrl());
            preparedStatement.executeUpdate();
            long pageId = 0;
            if (preparedStatement.getGeneratedKeys().next()) {
                pageId = preparedStatement.getGeneratedKeys().getLong(1);
                preparedStatement.close();
            }
            preparedStatement = connection.prepareStatement(SQL_INSERT_WORD);
            Map<String, Integer> words = page.getWords();
            for (Map.Entry<String, Integer> pair : words.entrySet()) {
                String word = pair.getKey().replaceAll("'", "");
                Integer count = pair.getValue();
                preparedStatement.setString(1, word);
                preparedStatement.setInt(2, count);
                preparedStatement.setLong(3, pageId);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();
            return true;
        } catch (SQLException sqlException) {
            logger.error("Error saving the page in the database.", sqlException);
            return false;
        } finally {
            closeConnection();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            getConnection();
            preparedStatement = connection.prepareStatement(SQL_DELETE_PAGE);
            preparedStatement.setLong(1, id);
            if (preparedStatement.executeUpdate() == 0) {
                throw new PageNotFoundException(id);
            }
            preparedStatement.close();
            logger.info("Page with ID: " + id + " deleted.");
            return true;
        } catch (SQLException sqlException) {
            logger.error("Page deletion error.", sqlException);
            return false;
        } catch (PageNotFoundException pageNotFoundException) {
            logger.info("Page with id " + id + " not found.");
            return false;
        } finally {
            closeConnection();
        }
    }
}