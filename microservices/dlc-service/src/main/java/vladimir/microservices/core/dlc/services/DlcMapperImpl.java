package vladimir.microservices.core.dlc.services;

import java.util.ArrayList;
import java.util.List;
import vladimir.api.core.dlc.Dlc;
import vladimir.microservices.core.dlc.persistence.DlcEntity;
import org.springframework.stereotype.Component;

@Component
public class DlcMapperImpl implements DlcMapper
{
    public Dlc entityToApi(final DlcEntity entity) {
        if (entity == null) {
            return null;
        }
        final Dlc dlc = new Dlc();
        dlc.setDlcId(entity.getDlcId());
        dlc.setGameId(entity.getGameId());
        dlc.setName(entity.getName());
        dlc.setPrice(entity.getPrice());
        return dlc;
    }
    
    public DlcEntity apiToEntity(final Dlc api) {
        if (api == null) {
            return null;
        }
        final DlcEntity dlcEntity = new DlcEntity();
        dlcEntity.setGameId(api.getGameId());
        dlcEntity.setDlcId(api.getDlcId());
        dlcEntity.setName(api.getName());
        dlcEntity.setPrice(api.getPrice());
        return dlcEntity;
    }
    
    public List<Dlc> entityListToApiList(final List<DlcEntity> entity) {
        if (entity == null) {
            return null;
        }
        final List<Dlc> list = new ArrayList<Dlc>(entity.size());
        for (final DlcEntity dlcEntity : entity) {
            list.add(this.entityToApi(dlcEntity));
        }
        return list;
    }
    
    public List<DlcEntity> apiListToEntityList(final List<Dlc> api) {
        if (api == null) {
            return null;
        }
        final List<DlcEntity> list = new ArrayList<DlcEntity>(api.size());
        for (final Dlc dlc : api) {
            list.add(this.apiToEntity(dlc));
        }
        return list;
    }
}