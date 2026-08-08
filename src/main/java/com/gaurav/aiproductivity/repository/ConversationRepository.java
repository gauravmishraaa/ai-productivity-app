package com.gaurav.aiproductivity.repository;

import com.gaurav.aiproductivity.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository
        extends JpaRepository<Conversation, Long> {
}