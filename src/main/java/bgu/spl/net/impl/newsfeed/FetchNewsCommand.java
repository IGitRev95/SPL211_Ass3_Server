//package bgu.spl.net.impl.newsfeed;
//
//import java.io.Serializable;
//
//public class FetchNewsCommand implements Command<NewsFeed> {
//
//    private String channel;
//
//    public FetchNewsCommand(String channel) {
//        this.channel = channel;
//    }
//
//    @Override
//    public Serializable execute(NewsFeed feed) {
//        return feed.fetch(channel);
//    }
//
//}
