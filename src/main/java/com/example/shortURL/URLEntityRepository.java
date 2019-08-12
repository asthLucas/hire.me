package com.example.shortURL;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface URLEntityRepository extends JpaRepository<URLEntity, Long>{

	public URLEntity findByOriginalURLOrAlias(String originalURL, String alias);
}
