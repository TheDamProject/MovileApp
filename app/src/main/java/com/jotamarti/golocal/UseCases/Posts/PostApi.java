package com.jotamarti.golocal.UseCases.Posts;

import com.jotamarti.golocal.Models.Post;

public interface PostApi {

    void createPostInBackend(Post post, PostCallbacks.onResponseCreatePostInBackend onResponseCreatePostInBackend);
}
