INSERT INTO LU_LICENSE_TYPE(CODE, DESCRIPTION) VALUES ('Q1', 'Rehab Counseling Certification');
INSERT INTO LU_LICENSE_TYPE(CODE, DESCRIPTION) VALUES ('Q2', 'Psychosocial Rehab Practitioner Certification');

INSERT INTO PROVIDER_SETTING(PROVIDER_SETTING_ID, PROVIDER_TYP_CD, RELATED_ENTITY_TYP, RELATED_ENTITY_CD, REL_TYP)
    VALUES (2003, '20', 'LicenseType', 'Q1', 'LO');
INSERT INTO PROVIDER_SETTING(PROVIDER_SETTING_ID, PROVIDER_TYP_CD, RELATED_ENTITY_TYP, RELATED_ENTITY_CD, REL_TYP)
    VALUES (2004, '20', 'LicenseType', 'Q2', 'LO');