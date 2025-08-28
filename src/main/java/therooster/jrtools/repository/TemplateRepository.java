package therooster.jrtools.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import therooster.jrtools.entity.ReportTemplate;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<ReportTemplate, Integer> {
    Optional<ReportTemplate> findByTag(String tag);
    boolean existsByTag(String tag);

}
