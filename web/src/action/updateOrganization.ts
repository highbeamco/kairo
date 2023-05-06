import { organizationApiState } from 'api/OrganizationApi';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import OrganizationRep from 'rep/OrganizationRep';
import organizationsState from 'state/admin/organizations';
import organizationState from 'state/core/organization';
import organizationGuidState from 'state/core/organizationGuid';
import { useSetRecoilStateIfActive } from 'state/util/useSetRecoilStateIfActive';

type UpdateOrganization = (updater: OrganizationRep.Updater) => Promise<OrganizationRep>;

const useUpdateOrganization = (organizationGuid: string): UpdateOrganization => {
  const organizationApi = useRecoilValue(organizationApiState);

  const setOrganizations = useSetRecoilStateIfActive(organizationsState);

  const currentOrganizationGuid = useRecoilValue(organizationGuidState);
  const setCurrentOrganization = useSetRecoilState(organizationState);

  return async (updater): Promise<OrganizationRep> => {
    const organization = await organizationApi.update(organizationGuid, updater);
    setOrganizations((currVal) => new Map(currVal).set(organization.guid, organization));
    if (organizationGuid === currentOrganizationGuid) {
      setCurrentOrganization(organization);
    }
    return organization;
  };
};

export default useUpdateOrganization;
