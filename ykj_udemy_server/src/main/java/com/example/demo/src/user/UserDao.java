package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into user (name, nickName, phone, email, password) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getNickName(),postUserReq.getPhone(), postUserReq.getEmail(), postUserReq.getPassword()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from user where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }


    public int checkUser(int userIdx) {
        String checkUserQuery = "select exists(select userIdx from user where userIdx = ? and status = 'ACTIVE')";
        int checkUserParam = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserQuery,
                int.class,
                checkUserParam);
    }



}
