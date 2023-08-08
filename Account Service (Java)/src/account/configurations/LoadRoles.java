package account.configurations;

import account.models.Group;
import account.repositories.GroupRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadRoles {
    private final GroupRepository groupRepository;

    public LoadRoles(GroupRepository groupRepository) {
        this.groupRepository =groupRepository;
        createRoles();
    }

    private void createRoles() {
        if (groupRepository.findAll().isEmpty()) {
            groupRepository.save(new Group("ROLE_ADMINISTRATOR"));
            groupRepository.save(new Group("ROLE_USER"));
            groupRepository.save(new Group("ROLE_ACCOUNTANT"));
            groupRepository.save(new Group("ROLE_AUDITOR"));
        }
    }
}
