StringRequest sr = new StringRequest(Request.Method.POST,"http://api.someservice.com/post/comment", new Response.Listener<String>() {
@Override
public void onResponse(String response) {
        mPostCommentResponse.requestCompleted();
        }
        }, new Response.ErrorListener() {
@Override
public void onErrorResponse(VolleyError error) {
        mPostCommentResponse.requestEndedWithError(error);
        }
        }){
@Override
protected Map<String,String> getParams(){
        Map<String,String> params = new HashMap<String, String>();
        params.put("user",userAccount.getUsername());
        params.put("pass",userAccount.getPassword());
        params.put("comment", Uri.encode(comment));
        params.put("comment_post_ID",String.valueOf(postId));
        params.put("blogId",String.valueOf(blogId));

        return params;
        }

@Override
public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type","application/x-www-form-urlencoded");
        return params;
        }
        };