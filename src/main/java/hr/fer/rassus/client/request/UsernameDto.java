package hr.fer.rassus.client.request;

public class UsernameDto {
    String username;

    public UsernameDto(String username) {
        this.username = username;
    }

    public UsernameDto() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "RegisterUsernameDto{" +
                "username='" + username + '\'' +
                '}';
    }
}
