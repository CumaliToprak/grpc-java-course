package blog.client;

import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BlogClient {

    private static void updateBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId)
    {
        try {
            Blog newBlog = Blog.newBuilder()
                    .setId(blogId.getId())
                    .setTitle("This is updated blog")
                    .setAuthor("New blog changed")
                    .setContent("cumali2")
                    .build();
            stub.updateBlog(newBlog);
            System.out.println("Blog updated: "+newBlog);
        }catch (StatusRuntimeException e)
        {
            System.out.println("Couldn't update the blog");
            e.printStackTrace();
        }
    }
    private static void readBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId)
    {
        try {
            Blog readResponse = stub.readBlog(blogId);
            System.out.println("Blog read: "+readResponse);
        }catch (StatusRuntimeException e)
        {
            System.out.println("Couldn't read the blog");
            e.printStackTrace();
        }
    }
    private static BlogId createBlog(BlogServiceGrpc.BlogServiceBlockingStub stub)
    {
        try {
            var createResponse = stub.createBlog(
                    Blog.newBuilder()
                            .setAuthor("Cumali")
                            .setTitle("New Blog!")
                            .setContent("This is the first blog!")
                            .build()
            );
            System.out.println("Blog created: "+createResponse.getId());
            return createResponse;
        }catch (StatusRuntimeException e)
        {
            System.out.println("Could't create the blog");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 5003)
                .usePlaintext() //we do not want to stuggle with ssl for now
                .build();

        run(channel);
        System.out.println("Shutting down");
        channel.shutdown();
    }

    private static void run(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);
        BlogId blogId = createBlog(stub);

        if(blogId == null) return;

        readBlog(stub, blogId);
        updateBlog(stub, blogId);

    }
}
