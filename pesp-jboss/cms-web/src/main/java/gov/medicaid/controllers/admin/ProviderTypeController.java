/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.medicaid.controllers.admin;

import gov.medicaid.controllers.ControllerHelper;
import gov.medicaid.entities.AgreementDocument;
import gov.medicaid.entities.AgreementDocumentSearchCriteria;
import gov.medicaid.entities.AgreementDocumentType;
import gov.medicaid.entities.ProviderType;
import gov.medicaid.entities.ProviderTypeSearchCriteria;
import gov.medicaid.entities.RequiredField;
import gov.medicaid.entities.SearchResult;
import gov.medicaid.services.AgreementDocumentService;
import gov.medicaid.services.LookupService;
import gov.medicaid.services.PortalServiceConfigurationException;
import gov.medicaid.services.PortalServiceException;
import gov.medicaid.services.ProviderTypeService;
import gov.medicaid.services.util.LogUtil;
import gov.medicaid.services.util.Util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * This controller class that manages a provider type.
 * </p>
 * <p>
 * This class is mutable and not thread safe, but used in thread safe manner by framework.
 * </p>
 *
 * @author argolite, TCSASSEMBLER
 * @version 1.0
 */
public class ProviderTypeController extends BaseServiceAdminController {

    /**
     * Represents the provider type service. it is managed with a getter and setter. It may have any value, but is
     * expected to be set to a non-null/empty value by dependency injection. It is fully mutable, but not expected to
     * change after dependency injection.
     */
    private ProviderTypeService providerTypeService;

    /**
     * Represents the agreement document service. it is managed with a getter and setter. It may have any value, but is
     * expected to be set to a non-null/empty value by dependency injection. It is fully mutable, but not expected to
     * change after dependency injection.
     */
    private AgreementDocumentService agreementDocumentService;

    /**
     * Lookup service.
     */
    private LookupService lookupService;

    /**
     * Empty constructor.
     */
    public ProviderTypeController() {
    }

    /**
     * This method checks that all required injection fields are in fact provided.
     *
     * @throws PortalServiceConfigurationException If there are required injection fields that are not injected
     */
    @Override
    @PostConstruct
    protected void init() {
        super.init();

        if (lookupService == null) {
            throw new PortalServiceConfigurationException("lookupService must be configured.");
        }

        if (providerTypeService == null) {
            throw new PortalServiceConfigurationException("providerTypeService must be configured.");
        }

        if (agreementDocumentService == null) {
            throw new PortalServiceConfigurationException("agreementDocumentService must be configured.");
        }
    }

    /**
     * This action will load the provider types page.
     *
     * @return the model and view instance that contains the name of view to be rendered and data to be used for
     *         rendering (not null)
     * @throws PortalServiceException If there are any errors in the action
     */
    @RequestMapping(value = "/admin/viewProviderTypes", method = RequestMethod.GET)
    public ModelAndView view() throws PortalServiceException {
        String signature = "ProviderTypeController#view()";
        LogUtil.traceEntry(getLog(), signature, null, null);

        try {
            ProviderTypeSearchCriteria criteria = new ProviderTypeSearchCriteria();
            criteria.setPageNumber(1);
            criteria.setPageSize(10);
            criteria.setSortColumn("description");

            SearchResult<ProviderType> result = providerTypeService.search(criteria);
            ModelAndView model = new ModelAndView("admin/service_admin_provider_types");
            model.addObject("providerTypesSearchResult", result);
            model.addObject("searchCriteria", criteria);
            return LogUtil.traceExit(getLog(), signature, model);
        } catch (PortalServiceException e) {
            LogUtil.traceError(getLog(), signature, e);
            throw e;
        }
    }

    /**
     * This action will search for provider types.
     *
     * @param criteria the search criteria
     * @return the model and view instance that contains the name of view to be rendered and data to be used for
     *         rendering (not null)
     * @throws PortalServiceException If there are any errors in the action
     */
    @RequestMapping(value = "/admin/viewProviderTypes", method = RequestMethod.POST)
    public ModelAndView search(@ModelAttribute("criteria") ProviderTypeSearchCriteria criteria)
        throws PortalServiceException {
        String signature = "ProviderTypeController#search(ProviderTypeSearchCriteria criteria)";
        LogUtil.traceEntry(getLog(), signature, new String[] {"criteria"}, new Object[] {criteria});

        try {
            SearchResult<ProviderType> result = providerTypeService.search(criteria);
            ModelAndView model = new ModelAndView("admin/service_admin_provider_types");
            model.addObject("providerTypesSearchResult", result);
            model.addObject("searchCriteria", criteria);
            return LogUtil.traceExit(getLog(), signature, model);
        } catch (PortalServiceException e) {
            LogUtil.traceError(getLog(), signature, e);
            throw e;
        }
    }

    /**
     * This method is used to build the contained required fields string.
     *
     * @param providerType the provider type
     * @return the string representation of the contained required fields.
     */
    private static final String buildContainedRequiredFieldsString(ProviderType providerType) {
        StringBuilder containedRequiredFields = new StringBuilder();
        if (providerType.getRequiredFields() != null) {
            for (RequiredField requiredField : providerType.getRequiredFields()) {
                containedRequiredFields.append("@").append(requiredField.getType().getDescription())
                    .append(requiredField.getDescription()).append("@");
            }
        }
        return containedRequiredFields.toString();
    }

    /**
     * This action will get the entity with the given ID.
     *
     * @param providerTypeId the entity ID
     * @return the model and view instance that contains the name of view to be rendered and data to be used for
     *         rendering (not null)
     * @throws PortalServiceException If there are any errors in the action
     */
    @RequestMapping(value = "/admin/getProviderType", method = RequestMethod.GET)
    public ModelAndView get(@RequestParam("providerTypeId") String providerTypeId) throws PortalServiceException {
        String signature = "ProviderTypeController#get(long providerTypeId)";
        LogUtil.traceEntry(getLog(), signature, new String[] {"providerTypeId"}, new Object[] {providerTypeId});

        try {
            ProviderType providerType = providerTypeService.get(providerTypeId);
            ModelAndView model = new ModelAndView("admin/service_admin_view_provider_type");
            model.addObject("providerType", providerType);

            model.addObject("containedRequiredFields", buildContainedRequiredFieldsString(providerType));
            return LogUtil.traceExit(getLog(), signature, model);
        } catch (PortalServiceException e) {
            LogUtil.traceError(getLog(), signature, e);
            throw e;
        }
    }

    /**
     * This action will prepare for the creation of the entity.
     *
     * @return the model and view instance that contains the name of view to be rendered and data to be used for
     *         rendering (not null)
     * @throws PortalServiceException If there are any errors in the action
     */
    @RequestMapping(value = "/admin/beginCreateProviderType", method = RequestMethod.GET)
    public ModelAndView beginCreate() throws PortalServiceException {
        String signature = "ProviderTypeController#beginCreate()";
        LogUtil.traceEntry(getLog(), signature, null, null);

        try {
            // Retrieve agreements
            AgreementDocumentSearchCriteria criteria = new AgreementDocumentSearchCriteria();
            criteria.setPageNumber(1);
            criteria.setPageSize(-1);
            criteria.setType(AgreementDocumentType.AGREEMENT);

            List<AgreementDocument> agreements = agreementDocumentService.search(criteria).getItems();
            // Retrieve addendums
            criteria = new AgreementDocumentSearchCriteria();
            criteria.setPageNumber(1);
            criteria.setPageSize(-1);
            criteria.setType(AgreementDocumentType.ADDENDUM);

            List<AgreementDocument> addendums = agreementDocumentService.search(criteria).getItems();
            ModelAndView model = new ModelAndView("admin/service_admin_create_provider_type");
            model.addObject("agreements", agreements);
            model.addObject("addendums", addendums);
            model.addObject("providerType", new ProviderType());
            return LogUtil.traceExit(getLog(), signature, model);
        } catch (PortalServiceException e) {
            LogUtil.traceError(getLog(), signature, e);
            throw e;
        }
    }

    /**
     * This action will get the entity with the given ID.
     *
     * @param providerTypeId the entity ID
     * @return the model and view instance that contains the name of view to be rendered and data to be used for
     *         rendering (not null)
     * @throws PortalServiceException If there are any errors in the action
     */
    @RequestMapping(value = "/admin/beginEditProviderType", method = RequestMethod.GET)
    public ModelAndView beginEdit(@RequestParam("providerTypeId") String providerTypeId) throws PortalServiceException {
        String signature = "ProviderTypeController#beginEdit(long providerTypeId)";
        LogUtil.traceEntry(getLog(), signature, new String[] {"providerTypeId"}, new Object[] {providerTypeId});

        try {
            ProviderType providerType = providerTypeService.get(providerTypeId);

            // Retrieve agreements
            AgreementDocumentSearchCriteria criteria = new AgreementDocumentSearchCriteria();
            criteria.setPageNumber(1);
            criteria.setPageSize(-1);
            criteria.setType(AgreementDocumentType.AGREEMENT);

            List<AgreementDocument> agreements = agreementDocumentService.search(criteria).getItems();
            // Retrieve addendums
            criteria = new AgreementDocumentSearchCriteria();
            criteria.setPageNumber(1);
            criteria.setPageSize(-1);
            criteria.setType(AgreementDocumentType.ADDENDUM);

            List<AgreementDocument> addendums = agreementDocumentService.search(criteria).getItems();
            ModelAndView model = new ModelAndView("admin/service_admin_edit_provider_type");
            model.addObject("providerType", providerType);
            model.addObject("agreements", agreements);
            model.addObject("addendums", addendums);
            model.addObject("containedRequiredFields", buildContainedRequiredFieldsString(providerType));
            return LogUtil.traceExit(getLog(), signature, model);
        } catch (PortalServiceException e) {
            LogUtil.traceError(getLog(), signature, e);
            throw e;
        }
    }

    /**
     * This method is used to retrieve required fields.
     *
     * @param providerType the provider type
     * @return a list of the RequiredField
     * @throws PortalServiceException if error occurred when calling lookup service.
     */
    private List<RequiredField> retrieveRequiredFields(ProviderType providerType) throws PortalServiceException {
        if (providerType.getRequiredFields() == null) {
            return null;
        }
        List<RequiredField> requiredFields = lookupService.findAllLookups(RequiredField.class);
        List<RequiredField> includedFields = new ArrayList<RequiredField>();
        for (RequiredField reqField : providerType.getRequiredFields()) {
            for (RequiredField requiredField : requiredFields) {
                if (requiredField.getDescription().equals(reqField.getDescription())
                    && requiredField.getType().getDescription().equals(reqField.getType().getDescription())) {
                    includedFields.add(requiredField);
                }
            }
        }
        return includedFields;
    }

    /**
     * This action will create the entity.
     *
     * @param providerType the providerType
     * @param request the http servlet request
     * @return the model and view for creationg result
     * @throws IllegalArgumentException if providerType is null/empty
     * @throws PortalServiceException If there are any errors in the action
     */
    @RequestMapping(value = "/admin/createProviderType", method = RequestMethod.POST)
    public ModelAndView create(@ModelAttribute("providerType") ProviderType providerType, HttpServletRequest request)
        throws PortalServiceException {
        String signature = "ProviderTypeController#create(ProviderType providerType)";
        LogUtil.traceEntry(getLog(), signature, new String[] {"providerType"}, new Object[] {providerType});

        try {
            boolean blank = Util.isBlank(providerType.getDescription());
            boolean exists = getLookupService().findLookupByDescription(ProviderType.class, providerType.getDescription()) != null;
            if (!blank && !exists) {
                providerType.setRequiredFields(retrieveRequiredFields(providerType));
                
                providerTypeService.create(providerType);
                
                // Retrieve
                providerType = providerTypeService.get(providerType.getCode());
                
                ModelAndView model = new ModelAndView("admin/service_admin_view_provider_type");
                model.addObject("providerType", providerType);
                model.addObject("containedRequiredFields", buildContainedRequiredFieldsString(providerType));
                return LogUtil.traceExit(getLog(), signature, model);
            } else {
                
                ModelAndView mv = beginCreate();
                if (blank) {
                    ControllerHelper.addError("Please specify a provider type.");
                } else if (exists) {
                    ControllerHelper.addError("Specified provider type already exists.");
                }
                mv.addObject("providerType", providerType);
                return LogUtil.traceExit(getLog(), signature, mv);
            }
        } catch (PortalServiceException e) {
            LogUtil.traceError(getLog(), signature, e);
            throw e;
        }
    }

    /**
     * This action will save the entity.
     *
     * @param providerType the providerType
     * @param request the http servlet request
     * @return the model and view for update result
     * @throws IllegalArgumentException if providerType is null/empty
     * @throws PortalServiceException If there are any errors in the action
     */
    @RequestMapping(value = "/admin/updateProviderType", method = RequestMethod.POST)
    public ModelAndView edit(@ModelAttribute("providerType") ProviderType providerType, HttpServletRequest request)
        throws PortalServiceException {
        String signature = "ProviderTypeController#edit(ProviderType providerType)";
        LogUtil.traceEntry(getLog(), signature, new String[] {"providerType"}, new Object[] {providerType});

        try {
            providerType.setRequiredFields(retrieveRequiredFields(providerType));
            providerTypeService.update(providerType);
            // Retrieve
            providerType = providerTypeService.get(providerType.getCode());

            ModelAndView model = new ModelAndView("admin/service_admin_view_provider_type");
            model.addObject("providerType", providerType);
            model.addObject("containedRequiredFields", buildContainedRequiredFieldsString(providerType));
            return LogUtil.traceExit(getLog(), signature, model);
        } catch (PortalServiceException e) {
            LogUtil.traceError(getLog(), signature, e);
            throw e;
        }
    }

    /**
     * This action will delete the entities with the given IDs from the single view.
     *
     * @param providerTypeIds the entity IDs
     * @param request the http servlet request
     * @return the successful text
     * @throws PortalServiceException If there are any errors in the action
     */
    @RequestMapping(value = "/admin/deleteProviderTypes", method = RequestMethod.GET)
    @ResponseBody
    public String delete(@RequestParam("providerTypeIds") long[] providerTypeIds, HttpServletRequest request)
        throws PortalServiceException {
        String signature = "ProviderTypeController#delete(long[] providerTypeIds)";
        LogUtil.traceEntry(getLog(), signature, new String[] {"providerTypeIds"}, new Object[] {providerTypeIds});

        try {
            for (long providerTypeId : providerTypeIds) {
                providerTypeService.delete(providerTypeId);
            }
            return LogUtil.traceExit(getLog(), signature, "success");
        } catch (PortalServiceException e) {
            LogUtil.traceError(getLog(), signature, e);
            throw e;
        }
    }

    /**
     * Getter of providerTypeService.
     *
     * @return the providerTypeService
     */
    public ProviderTypeService getProviderTypeService() {
        return providerTypeService;
    }

    /**
     * Set the providerTypeService.
     *
     * @param providerTypeService the providerTypeService to set
     */
    public void setProviderTypeService(ProviderTypeService providerTypeService) {
        this.providerTypeService = providerTypeService;
    }

    /**
     * Getter of agreementDocumentService.
     *
     * @return the agreementDocumentService
     */
    public AgreementDocumentService getAgreementDocumentService() {
        return agreementDocumentService;
    }

    /**
     * The getter for the lookupService instance variable.
     *
     * @return the lookupService
     */
    public LookupService getLookupService() {
        return lookupService;
    }

    /**
     * The setter for the lookupService instance variable.
     *
     * @param lookupService the lookupService to set
     */
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    /**
     * Set the agreementDocumentService.
     *
     * @param agreementDocumentService the agreementDocumentService to set
     */
    public void setAgreementDocumentService(AgreementDocumentService agreementDocumentService) {
        this.agreementDocumentService = agreementDocumentService;
    }
}
