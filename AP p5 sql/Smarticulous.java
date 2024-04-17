package smarticulous;

import smarticulous.db.Exercise;
import smarticulous.db.Submission;
import smarticulous.db.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;

/**
 * The Smarticulous class, implementing a grading system.
 */
public class Smarticulous {

    /**
     * The connection to the underlying DB.
     * <p>
     * null if the db has not yet been opened.
     */
    Connection db;

    /**
     * Open the {@link Smarticulous} SQLite database.
     * <p>
     * This should open the database, creating a new one if necessary, and set the {@link #db} field
     * to the new connection.
     * <p>
     * The open method should make sure the database contains the following tables, creating them if necessary:
     *
     * <table>
     *   <caption><em>Table name: <strong>User</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>UserId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Username</td><td>Text</td></tr>
     *   <tr><td>Firstname</td><td>Text</td></tr>
     *   <tr><td>Lastname</td><td>Text</td></tr>
     *   <tr><td>Password</td><td>Text</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Exercise</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>DueDate</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Question</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>Desc</td><td>Text</td></tr>
     *   <tr><td>Points</td><td>Integer</td></tr>
     * </table>
     * In this table the combination of ExerciseId and QuestionId together comprise the primary key.
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Submission</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>UserId</td><td>Integer</td></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>SubmissionTime</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>QuestionGrade</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Grade</td><td>Real</td></tr>
     * </table>
     * In this table the combination of SubmissionId and QuestionId together comprise the primary key.
     *
     * @param dburl The JDBC url of the database to open (will be of the form "jdbc:sqlite:...")
     * @return the new connection
     * @throws SQLException
     */
    public Connection openDB(String dburl) throws SQLException {
        db = DriverManager.getConnection(dburl);
        try {
            Statement statement = db.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS User " +
                    "(UserId INTEGER PRIMARY KEY, " +
                    "Username TEXT UNIQUE, " +
                    "firstname TEXT, " +
                    "Lastname TEXT, " +
                    "Password TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Exercise" +
                    "(ExerciseId INTEGER PRIMARY KEY, " +
                    "Name TEXT, " +
                    "DueDate INTEGER)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Question" +
                    "(ExerciseId INTEGER, " +
                    "QuestionId INTEGER, " +
                    "Name TEXT, " +
                    "Desc TEXT, " +
                    "Points INTEGER, " +
                    "PRIMARY KEY (ExerciseId, QuestionId))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Submission" +
                    "(SubmissionId INTEGER PRIMARY KEY, " +
                    "UserId INTEGER, " +
                    "ExerciseId INTEGER, " +
                    "SubmissionTime INTEGER)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS QuestionGrade" +
                    "(SubmissionId INTEGER, " +
                    "QuestionId INTEGER, " +
                    "Grade REAL, " +
                    "PRIMARY KEY (SubmissionId, QuestionId))");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return db;
    }



    /**
     * Close the DB if it is open.
     *
     * @throws SQLException
     */
    public void closeDB() throws SQLException {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    // =========== User Management =============

    /**
     * Add a user to the database / modify an existing user.
     * <p>
     * Add the user to the database if they don't exist. If a user with user.username does exist,
     * update their password and firstname/lastname in the database.
     *
     * @param user
     * @param password
     * @return the userid.
     * @throws SQLException
     */
    public int addOrUpdateUser(User user, String password) throws SQLException {
        String checkUser=("SELECT UserId, Username FROM user WHERE Username = ?");
        int currId;
        try(
            PreparedStatement checkUserStatment = db.prepareStatement(checkUser)){
            checkUserStatment.setString(1, user.username);
            ResultSet res = checkUserStatment.executeQuery();
            if(!res.next()){//user dont exict
                String newUser = ("INSERT INTO User (Username, Firstname, Lastname, Password) VALUES (?,?,?,?)");
                try(
                    PreparedStatement addUser = db.prepareStatement(newUser)){
                    addUser.setString(1, user.username);
                    addUser.setString(2, user.firstname);
                    addUser.setString(3, user.lastname);
                    addUser.setString(4, password);
                    addUser.executeUpdate();
                }

            }
            else{//user exist
                String updateUser = "UPDATE User SET Firstname=?, Lastname=?, Password=? WHERE Username=?";
                try(PreparedStatement updateUserStat = db.prepareStatement(updateUser)) {
                    updateUserStat.setString(1, user.firstname);
                    updateUserStat.setString(2, user.lastname);
                    updateUserStat.setString(3, password);
                    updateUserStat.setString(4, user.username);
                    updateUserStat.executeUpdate();
                }
            }
            ResultSet afterRes = checkUserStatment.executeQuery();
            currId = afterRes.getInt("UserId");
            return currId;

        }

        catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * Verify a user's login credentials.
     *
     * @param username
     * @param password
     * @return true if the user exists in the database and the password matches; false otherwise.
     * @throws SQLException
     * <p>
     * Note: this is totally insecure. For real-life password checking, it's important to store only
     * a password hash
     * @see <a href="https://crackstation.net/hashing-security.htm">How to Hash Passwords Properly</a>
     */
    public boolean verifyLogin(String username, String password) throws SQLException {
        String loginCheck = "SELECT Username,Password from User WHERE Username = ? AND Password = ?";
        try(
            PreparedStatement login = db.prepareStatement(loginCheck)){
            login.setString(1, username);
            login.setString(2, password);
            ResultSet res = login.executeQuery();
            if(res.next()) // there is a matching username and password
                return true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // =========== Exercise Management =============

    /**
     * Add an exercise to the database.
     *
     * @param exercise
     * @return the new exercise id, or -1 if an exercise with this id already existed in the database.
     * @throws SQLException
     */
    public int addExercise(Exercise exercise) throws SQLException {
        //check if there is exercise with the same id
        String checkQ = "SELECT * FROM Exercise WHERE ExerciseID = ?";
        try (
            PreparedStatement checkSt = db.prepareStatement(checkQ)){
            checkSt.setInt(1, exercise.id);
            try (ResultSet res1 = checkSt.executeQuery()){
                if (res1.next()) //the exercise exist already
                    return -1;
            }
        }

            //add the exercise
            String addquary = "INSERT INTO Exercise (ExerciseId, Name, DueDate) VALUES (?, ?, ?)";
            try(PreparedStatement addExe = db.prepareStatement(addquary)) {
                addExe.setInt(1, exercise.id);
                addExe.setString(2, exercise.name);
                addExe.setLong(3, exercise.dueDate.getTime());//turn the date to the milisecond since 1970
                addExe.executeUpdate();
            }

            //add all questions from this exercise to questions table
            String addQuestion = "INSERT INTO Question (ExerciseID, Name, Desc, Points) VALUES (?, ?, ?, ?)";
            try(PreparedStatement addQ = db.prepareStatement(addQuestion)) {
                addQ.setInt(1, exercise.id);
                for (Exercise.Question q : exercise.questions) {
                    addQ.setString(2, q.name);
                    addQ.setString(3, q.desc);
                    addQ.setInt(4, q.points);
                    addQ.executeUpdate();
                }
                return exercise.id;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
        }



    /**
     * Return a list of all the exercises in the database.
     * <p>
     * The list should be sorted by exercise id.
     *
     * @return list of all exercises.
     * @throws SQLException
     */
    public List<Exercise> loadExercises() throws SQLException {
        List<Exercise> exeList= new ArrayList<>();
        try (
            Statement statement = db.createStatement();
            ResultSet resExe = statement.executeQuery("SELECT * FROM Exercise")){
            while(resExe.next()){ //iterate on all exercises in table
                int currentId= resExe.getInt("ExerciseId");
                //create a temp exercise object with empty questions list
                Exercise currExe = new Exercise(currentId, resExe.getString("Name"), new Date(resExe.getInt("DueDate")));
                try(Statement statement1 = db.createStatement();
                    ResultSet resQuest= statement1.executeQuery("SELECT * FROM Question WHERE ExerciseId = "+currentId)) {
                    //iterate on all questions with this exercise id and add them
                    while (resQuest.next()) {
                        String currQName = resQuest.getString("Name");
                        String CurrQDesc = resQuest.getString("Desc");
                        int CurrQPoints = resQuest.getInt("Points");
                        currExe.addQuestion(currQName, CurrQDesc, CurrQPoints);
                    }
                }
                exeList.add(currExe);//add the built exercise object to the list
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return exeList;
    }

    // ========== Submission Storage ===============

    /**
     * Store a submission in the database.
     * The id field of the submission will be ignored if it is -1.
     * <p>
     * Return -1 if the corresponding user doesn't exist in the database.
     *
     * @param submission
     * @return the submission id.
     * @throws SQLException
     */
    public int storeSubmission(Submission submission) throws SQLException {
        String checkUser = "SELECT * FROM User WHERE Username = ?";
        int userId;
       try(PreparedStatement checkUs = db.prepareStatement(checkUser)){
           //check if the user exist
           checkUs.setString(1, submission.user.username);
           try(ResultSet checkRes = checkUs.executeQuery()) {
               if (!checkRes.next())
                   return -1;
               else
                   userId = checkRes.getInt("UserId"); //saves the user ID
           }

           String addSub = "INSERT INTO Submission (SubmissionId, UserId, ExerciseId, SubmissionTime) VALUES (?,?,?,?)";
           try(PreparedStatement addSubStat = db.prepareStatement(addSub, Statement.RETURN_GENERATED_KEYS)) {
               if (submission.id != -1)//ignore the sub id if it is -1
                   addSubStat.setInt(1, submission.id);
               addSubStat.setInt(2, userId);
               addSubStat.setInt(3, submission.exercise.id);
               addSubStat.setLong(4, submission.submissionTime.getTime());
               addSubStat.executeUpdate();
               if(submission.id!=-1)
                   return submission.id;
               try(ResultSet key = addSubStat.getGeneratedKeys()){//if this is a new submission, gets the key that was generated
                   if(key.next())
                       return key.getInt(1);
               }
           }

       }
       catch (SQLException e){
           e.printStackTrace();
       }
        return -1;
    }


    // ============= Submission Query ===============


    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the latest submission for the given exercise by the given user.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getLastSubmission(User, Exercise)}
     *
     * @return
     */
    PreparedStatement getLastSubmissionGradesStatement() throws SQLException {
        //join the submission, User, QustionGrade tabled using the id's, get the rows of the given username and exercise id,
        // and the rows that matches the latest submission time of this exercise of this user. and sort it according to the questionId

        String getLast =
                "SELECT " +
                    "s.SubmissionId, " +
                    "qg.QuestionId, " +
                    "qg.Grade, " +
                    "s.SubmissionTime " +
                "FROM " +
                    "Submission s INNER JOIN User u ON s.UserID = u.UserId " +
                    "INNER JOIN QuestionGrade qg ON s.SubmissionId = qg.SubmissionId "+
                "WHERE " +
                        "u.Username = ? " +
                        "AND s.ExerciseId = ? " +
                        "AND qg.QuestionId <= ? " +
                        "AND s.SubmissionTime = " +
                            "(SELECT SubmissionTime FROM Submission " +
                            "WHERE " + "UserId = u.userId AND ExerciseId = s.ExerciseId " +
                            "ORDER BY SubmissionTime DESC LIMIT 1) "+
                "ORDER BY " +
                        "qg.QuestionId ASC";
        PreparedStatement last = db.prepareStatement(getLast);
        return last;
        }




    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the <i>best</i> submission for the given exercise by the given user.
     * The best submission is the one whose point total is maximal.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getBestSubmission(User, Exercise)}
     *
     */
    PreparedStatement getBestSubmissionGradesStatement() throws SQLException {

        return null;
    }

    /**
     * Return a submission for the given exercise by the given user that satisfies
     * some condition (as defined by an SQL prepared statement).
     * <p>
     * The prepared statement should accept the user name as parameter 1, the exercise id as parameter 2 and a limit on the
     * number of rows returned as parameter 3, and return a row for each question corresponding to the submission, sorted by questionId.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @param stmt
     * @return
     * @throws SQLException
     */
    Submission getSubmission(User user, Exercise exercise, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, user.username);
        stmt.setInt(2, exercise.id);
        stmt.setInt(3, exercise.questions.size());

        ResultSet res = stmt.executeQuery();

        boolean hasNext = res.next();
        if (!hasNext)
            return null;

        int sid = res.getInt("SubmissionId");
        Date submissionTime = new Date(res.getLong("SubmissionTime"));

        float[] grades = new float[exercise.questions.size()];

        for (int i = 0; hasNext; ++i, hasNext = res.next()) {
            grades[i] = res.getFloat("Grade");
        }

        return new Submission(sid, user, exercise, submissionTime, (float[]) grades);
    }

    /**
     * Return the latest submission for the given exercise by the given user.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @return
     * @throws SQLException
     */
    public Submission getLastSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getLastSubmissionGradesStatement());
    }


    /**
     * Return the submission with the highest total grade
     *
     * @param user the user for which we retrieve the best submission
     * @param exercise the exercise for which we retrieve the best submission
     * @return
     * @throws SQLException
     */
    public Submission getBestSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getBestSubmissionGradesStatement());
    }
}
