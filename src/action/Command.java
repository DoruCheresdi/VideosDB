package action;

import common.Constants;
import data.Database;
import entertainment.Video;
import user.User;

/**
 * Class storing information and implementing logic for
 * the command action
 */
public class Command extends Action {
    private String type;
    private String user;
    private String title;
    private Double grade;
    private int season;

    public Command(final int actionID, final String type, final String user,
                   final String title, final Double grade, final int season) {
        super(actionID);
        this.type = type;
        this.user = user;
        this.title = title;
        this.grade = grade;
        this.season = season;
    }

    /**
     * Method that identifies the type of command to
     * be solved and calls the appropriate function
     */
    @Override
    public void solveAction() {
        switch (type) {
            case Constants.FAVORITE:
                solveFavoriteCommand();
                break;
            case Constants.VIEW:
                solveViewCommand();
                break;
            case Constants.RATING:
                solveRatingCommand();
                break;
            default:
                break;
        }

    }


    /**
     * Method to solve commands of the favorite type,
     * adds the film to the list of favorite videos
     * of the user
     */
    private void solveFavoriteCommand() {
        User soughtUser = findUser();

        // check if video is seen or already in the favorite list:
        if (!soughtUser.getHistory().containsKey(title)) {
            addMessageToJson("error -> " + title + " is not seen");
            return;
        } else if (soughtUser.getFavourite().contains(title)) {
            addMessageToJson("error -> " + title
                    + " is already in favourite list");
            return;
        }

        soughtUser.getFavourite().add(title);
        addMessageToJson("success -> " + title + " was added as favourite");
    }

    /**
     * Method to mark a video as viewed. If already
     * viewed, it will increment the number of views
     * in the user's history
     */
    private void solveViewCommand() {
        User soughtUser = findUser();
        if (soughtUser.getHistory().containsKey(title)) {
            soughtUser.getHistory()
                    .put(title, soughtUser.getHistory().get(title) + 1);
        } else {
            soughtUser.getHistory().put(title, 1);
        }

        addMessageToJson("success -> " + title
                + " was viewed with total views of " + soughtUser.getHistory().get(title));
    }

    /**
     * Method to add a rating to a video.
     * It checks if the video has been seen or if it has already been rated by the
     * user
     */
    private void solveRatingCommand() {
        User soughtUser = findUser();
        if (!soughtUser.getHistory().containsKey(title)) {
            addMessageToJson("error -> " + title + " is not seen");
            return;
        }

        Database database = Database.getDatabase();
        Video videoToRate = database.getVideos().stream().filter(video -> video.getName()
                .equals(title)).findAny().orElse(null);

        if (videoToRate.hasBeenRated(soughtUser, season)) {
            addMessageToJson("error -> " + title + " has been already rated");
            return;
        }

        soughtUser.getRatedVideos().put(videoToRate.getName(), season);
        videoToRate.addRating(grade, season);
        addMessageToJson("success -> " + title + " was rated with "
                + grade + " by " + user);
    }

    /**
     * Method used to find the user that does the command
     * @return
     */
    private User findUser() {
        Database database = Database.getDatabase();
        User soughtUser = database.getUsers().stream()
                .filter(user1 -> user1.getUsername().equals(user))
                .findAny().get();
        return soughtUser;
    }
}
