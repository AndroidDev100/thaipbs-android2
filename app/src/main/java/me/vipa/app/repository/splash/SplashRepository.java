package me.vipa.app.repository.splash;


public class SplashRepository {

    private static SplashRepository projectRepository;

    public synchronized static SplashRepository getInstance() {
        if (projectRepository == null) {
            projectRepository = new SplashRepository();
        }
        return projectRepository;
    }


}
