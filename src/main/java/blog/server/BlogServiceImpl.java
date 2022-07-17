package blog.server;

import com.google.protobuf.Empty;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;


import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private final MongoCollection<Document> mongoCollection;
    public BlogServiceImpl(MongoClient client) {
        //if does not find db and collection then it will create them
        MongoDatabase db = client.getDatabase("blogdb");
        mongoCollection = db.getCollection("blog");
    }

    @Override
    public void updateBlog(Blog request, StreamObserver<Empty> responseObserver) {
        if(request.getId().isEmpty())
        {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Blog id cannot be empty")
                    .asRuntimeException());
            return;
        }

        String id = request.getId();
        Document result = mongoCollection.findOneAndUpdate(
                eq("_id", new ObjectId(id)),
                combine(
                        set("author", request.getAuthor()),
                        set("tile", request.getTitle()),
                        set("content", request.getContent())
                )
        );
        if(result == null)
        {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("The blog was not found")
                    .augmentDescription("BlogId: "+request.getId())
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();

    }

    @Override
    public void createBlog(Blog request, StreamObserver<BlogId> responseObserver) {
        Document doc = new Document("author", request.getAuthor())
                .append("title", request.getTitle())
                .append("content", request.getContent());

        InsertOneResult result;

        try {
            result = mongoCollection.insertOne(doc);
        }catch (MongoException e)
        {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getLocalizedMessage())
                    .asRuntimeException());
            return; //quit the function, we dont want to do anything else
        }

        if(!result.wasAcknowledged() || result.getInsertedId() == null){
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Blog couldn't be created")
                    .asRuntimeException());
        }

        String id = result.getInsertedId().asObjectId().getValue().toString();

        responseObserver.onNext(BlogId.newBuilder().setId(id).build());
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(BlogId request, StreamObserver<Blog> responseObserver) {
        if(request.getId().isEmpty())
        {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Blog id cannot be empty")
                    .asRuntimeException());
            return;
        }

        String id = request.getId();
        Document doc = mongoCollection.find(eq("_id", new ObjectId(id))).first();

        if(doc == null)
        {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Blog was not found")
                    .augmentDescription("BlogId: "+id)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(Blog.newBuilder()
                .setAuthor(doc.getString("author"))
                .setTitle(doc.getString("title"))
                .setContent(doc.getString("content"))
                .build());

        responseObserver.onCompleted();
    }

}
