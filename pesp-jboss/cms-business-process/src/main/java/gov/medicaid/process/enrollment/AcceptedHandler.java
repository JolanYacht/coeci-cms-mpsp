/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.medicaid.process.enrollment;

import java.util.Date;

import gov.medicaid.binders.XMLUtility;
import gov.medicaid.domain.model.EnrollmentProcess;
import gov.medicaid.entities.CMSUser;
import gov.medicaid.entities.Enrollment;
import gov.medicaid.entities.Event;
import gov.medicaid.services.CMSConfigurator;
import gov.medicaid.services.FileNetService;
import gov.medicaid.services.PortalServiceException;
import gov.medicaid.services.ProviderEnrollmentService;
import gov.medicaid.services.SequenceGenerator;
import gov.medicaid.services.util.Sequences;
import gov.medicaid.services.util.XMLAdapter;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

/**
 * This initializes the application model.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class AcceptedHandler extends GenericHandler {

    /**
     * Provider service.
     */
    private final ProviderEnrollmentService providerService;

    /**
     * Entity manager.
     */
    private final EntityManager entityManager;

    /**
     * Sequence generator.
     */
    private final SequenceGenerator sequenceGenerator;

    /**
     * Filenet service.
     */
    private final FileNetService fileNetService;

    /**
     * Constructor using the fields.
     */
    public AcceptedHandler() {
        CMSConfigurator config = new CMSConfigurator();
        this.providerService = config.getEnrollmentService();
        this.entityManager = config.getPortalEntityManager();
        this.sequenceGenerator = config.getSequenceGenerator();
        this.fileNetService = config.getFileNetService();
    }

    /**
     * Initializes the process variable.
     * 
     * @param item
     *            the work item to abort
     * @param manager
     *            the work item manager
     */
    public void executeWorkItem(WorkItem item, WorkItemManager manager) {
        EnrollmentProcess model = (EnrollmentProcess) item.getParameter("model");
        String processInstanceId = model.getProcessAudit().getProcessInstanceId();

        String actorId = model.getEnrollment().getProviderInformation().getReviewedBy();
        Query query = entityManager.createQuery("FROM CMSUser where username = :username");
        query.setParameter("username", actorId);
        CMSUser user = (CMSUser) query.getSingleResult();

        long processId = Long.parseLong(processInstanceId);
        try {
            Enrollment ticket = providerService.getTicketByProcessInstanceId(user, processId);

            XMLAdapter.copyApprovalFieldsToTicket(model, ticket);
            providerService.approveTicketWithChanges(user, ticket);

            Event e = new Event();
            e.setCreatedBy(actorId);
            e.setCreatedOn(new Date());
            e.setId(sequenceGenerator.getNextValue(Sequences.EVENT_SEQ));
            e.setNpi(model.getEnrollment().getProviderInformation().getNPI());
            e.setStatus("04");
            e.setTicketId(ticket.getTicketId());
            entityManager.persist(e);

            item.getResults().put("model", model);
            manager.completeWorkItem(item.getId(), item.getResults());

            // Copy Files to FileNet
            fileNetService.exportFiles(model, ticket.getTicketId());
        } catch (PortalServiceException e) {
            XMLUtility.moveToStatus(model, actorId, "ERROR", "Approval process failed to completed.");
            abortWorkItem(item, manager);
        }
    }
}