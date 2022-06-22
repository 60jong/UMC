package com.example.demo.src.post;


import com.example.demo.src.post.model.GetPostImgRes;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.post.model.PostImgUrlReq;
import com.example.demo.src.post.model.PostPostsReq;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetPostsRes> selectPosts(int userIdx) {
        String selectPostsQuery = "select p.postIdx as postIdx,\n" +
                "u.userIdx as userIdx,\n" +
                "u.nickName as nickName,\n" +
                "u.profileImgUrl as profileImgUrl,\n" +
                "p.content as content,\n" +
                "if(postLikeCount is null, 0., postLikeCount) as postLikeCount,\n" +
                "if(commentCount is null, 0., commentCount) as commentCount,\n" +
                "case when timestampdiff(second, p.updatedAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(second, p.updatedAt, current_timestamp), '초 전')\n" +
                "when timestampdiff(minute, p.updatedAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(minute, p.updatedAt, current_timestamp), '분 전')\n" +
                "when timestampdiff(hour, p.updatedAt, current_timestamp) < 24\n" +
                "then concat(timestampdiff(hour, p.updatedAt, current_timestamp), '시간 전')\n" +
                "when timestampdiff(day, p.updatedAt, current_timestamp) < 365\n" +
                "then concat(timestampdiff(day, p.updatedAt, current_timestamp), '일 전')\n" +
                "else timestampdiff(year, p.updatedAt, current_timestamp)\n" +
                "end as updatedAt,\n" +
                "if(pl.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n" +
                "from post as p\n" +
                "join user as u on u.userIdx = p.userIdx\n" +
                "left join (select postIdx, userIdx, count(postLikeidx) as postLikeCount from postlike where status='ACTIVE') as pol on pol.postIdx = p.postIdx\n" +
                "left join (select postIdx, count(commentIdx) as commentCount from comment where status='ACTIVE') as cc on cc.postIdx = p.postIdx\n" +
                "left join follow as f on f.followerIdx = p.userIdx and f.status = 'ACTIVE'\n" +
                "left join postlike as pl on pl.userIdx = f.followeeIdx and pl.postIdx = p.postIdx\n" +
                "where f.followerIdx = ? and p.status = 'ACTIVE'\n" +
                "group by p.postIdx";

        String selectPostsImgsQuery = "select pi.postImgUrlIdx, pi.imgUrl\n" +
                "from postimgurl as pi\n" +
                "join post as p on p.postIdx = pi.postIdx\n" +
                "where pi.status = 'ACTIVE' and p.postIdx = ?";

        int selectPostsParam = userIdx;


        return this.jdbcTemplate.query(selectPostsQuery,
                (rs, rowNum) -> new GetPostsRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getInt("postLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                        this.jdbcTemplate.query(selectPostsImgsQuery,
                                (rk, rowKNum) -> new GetPostImgRes(
                                        rk.getInt("postImgUrlIdx"),
                                        rk.getString("imgUrl")),
                                rs.getInt("postIdx"))
                ),selectPostsParam);
    }
    public List<GetUserRes> getUsers(){
        String getUsersQuery = "select userIdx,name,nickName,email from user";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")
                ));
    }


    public GetUserRes selectUserByIdx(int userIdx){
        String selectUserByIdxQuery = "select userIdx,name,nickName,email from user where userIdx=? and status='ACTIVE'";
        int selectUserByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(selectUserByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                selectUserByIdxParams);
    }
    public GetUserInfoRes selectUserInfoByIdx(int userIdx) {
        String selectUserInfoByIdxQuery = "select u.userIdx as userIdx,\n" +
                "       u.name as name,\n" +
                "       u.nickName as nickName,\n" +
                "       u.birth as birth,\n" +
                "       u.gender as gender,\n" +
                "       u.website as website,\n" +
                "       u.profileImgUrl as profileImgUrl,\n" +
                "       u.introduction as introduction,\n" +
                "       u.email as email,\n" +
                "       if(followerCount is null, 0, followerCount) as followerCount,\n" +
                "       if(followingCount is null, 0, followingCount) as followingCount,\n" +
                "       p.postCount as postCount\n" +
                "from user as u\n" +
                "left join (select userIdx, count(postIdx) as postCount from post where status = 'ACTIVE' group by userIdx) p on p.userIdx = u.userIdx\n" +
                " left join (select followerIdx, count(followIdx) as followerCount from follow where status = 'ACTIVE' group by followerIdx) fc on fc.followerIdx = u.userIdx\n" +
                "left join (select followeeIdx, count(followIdx) as followingCount from follow where status = 'ACTIVE' group by followeeIdx) f on f.followeeIdx = u.userIdx\n" +
                "where u.userIdx = ? and u.status = 'ACTIVE'";

        int selectUserInfoByIdxParam = userIdx;
        return this.jdbcTemplate.queryForObject(selectUserInfoByIdxQuery,
                (rs, rowNum) -> new GetUserInfoRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("birth"),
                        rs.getString("gender"),
                        rs.getString("website"),
                        rs.getString("profileImgUrl"),
                        rs.getString("introduction"),
                        rs.getString("email"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        rs.getInt("postCount")),
                selectUserInfoByIdxParam);
    }

    public List<GetUserPostsRes> selectUserPostsByIdx(int userIdx) {
        String selectUserPostsByIdxQuery = "select p.postIdx as postIdx,\n" +
                "       pi.imgUrl as postImgUrl\n" +
                "from post as p\n" +
                "         join postimgurl pi on pi.postIdx = p.postIdx and pi.status = 'ACTIVE'\n" +
                "         join user as u on u.userIdx = p.userIdx\n" +
                "where p.status = 'ACTIVE' and u.userIdx = ?\n" +
                "group by p.postIdx\n" +
                "Having min(pi.postImgUrlIdx)\n" +
                "order by p.postIdx;";
        int selectUserPostsByIdxParam = userIdx;

        return this.jdbcTemplate.query(selectUserPostsByIdxQuery,
                (rs, rowNum) -> new GetUserPostsRes(
                        rs.getInt("postIdx"),
                        rs.getString("postImgUrl")
                ),selectUserPostsByIdxParam);
    }


    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from user where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }


    public int checkUser(int userIdx) {
        String checkUserQuery = "select exists(select userIdx from user where userIdx = ? and status = 'ACTIVE')";
        int checkUserParam = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserQuery,
                int.class,
                checkUserParam);
    }
    public int checkPost(int postIdx) {
        String checkPostExistQuery = "select exists(select postIdx from post where postIdx = ?)";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);
    }

    public int updatePost(int postIdx, String content) {

        String updatePostQuery = "update post set content = ? where postIdx = ?";
        Object[] updatePostParams = new Object[]{content, postIdx};

        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);
    }

    public int deletePost(int postIdx) {
        String deletePostQuery = "delete from post where postIdx = ?";
        Object[] deletePostParams = new Object[]{postIdx};

        return this.jdbcTemplate.update(deletePostQuery, deletePostParams);
    }

    public int insertPosts(PostPostsReq postPostsReq) {
        String insertPostsQuery = "insert into post(userIdx, content) values (?,?)";
        Object[] insertPostsParams = new Object[] {postPostsReq.getUserIdx(),postPostsReq.getContent()};

        this.jdbcTemplate.update(insertPostsQuery,insertPostsParams);

        String lastInsertIdxQuery= "select last_insert_id()";

        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }

    public void insertPostImgUrls(int postIdx, PostImgUrlReq postImgUrlReq) {
        String insertPostImgUrlsQuery = "insert into postimgurl(postIdx,imgUrl) values(?,?)";
        Object[] insertPostsImgUrlParams = new Object[] {postIdx,postImgUrlReq.getImgUrl()};

        this.jdbcTemplate.update(insertPostImgUrlsQuery, insertPostsImgUrlParams);
    }


    public int updatePostStatus(int postIdx) {
        String updatePostStatusQuery = "update post set status = 'INACTIVE' where postIdx = ?";
        Object[] updatePostStatusParam = new Object[]{postIdx};

        return this.jdbcTemplate.update(updatePostStatusQuery, updatePostStatusParam);
    }
}
