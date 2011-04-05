/*
 * This file is part of NavalPlan
 *
 * Copyright (C) 2009-2010 Fundación para o Fomento da Calidade Industrial e
 *                         Desenvolvemento Tecnolóxico de Galicia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.navalplanner.web.costcategories;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.navalplanner.business.common.IntegrationEntity;
import org.navalplanner.business.common.daos.IConfigurationDAO;
import org.navalplanner.business.common.entities.EntityNameEnum;
import org.navalplanner.business.common.exceptions.InstanceNotFoundException;
import org.navalplanner.business.common.exceptions.ValidationException;
import org.navalplanner.business.costcategories.daos.IHourCostDAO;
import org.navalplanner.business.costcategories.daos.ITypeOfWorkHoursDAO;
import org.navalplanner.business.costcategories.entities.TypeOfWorkHours;
import org.navalplanner.web.common.IntegrationEntityModel;
import org.navalplanner.web.common.concurrentdetection.OnConcurrentModification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Model for UI operations related to {@link TypeOfWorkHours}
 *
 * @author Jacobo Aragunde Perez <jaragunde@igalia.com>
 * @author Diego Pino García <dpino@igalia.com>
 */
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@OnConcurrentModification(goToPage = "/costcategories/typeOfWorkHours.zul")
public class TypeOfWorkHoursModel extends IntegrationEntityModel implements
        ITypeOfWorkHoursModel {

    private TypeOfWorkHours typeOfWorkHours;

    @Autowired
    private ITypeOfWorkHoursDAO typeOfWorkHoursDAO;

    @Autowired
    private IHourCostDAO hourCostDAO;

    @Autowired
    private IConfigurationDAO configurationDAO;

    @Override
    @Transactional
    public void confirmSave() throws ValidationException {
        typeOfWorkHoursDAO.save(typeOfWorkHours);
    }

    @Override
    @Transactional
    public void confirmRemove(TypeOfWorkHours typeOfWorkHours) {
        try {
            typeOfWorkHoursDAO.remove(typeOfWorkHours.getId());
        } catch (InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TypeOfWorkHours getTypeOfWorkHours() {
        return typeOfWorkHours;
    }

    @Override
    @Transactional(readOnly=true)
    public List<TypeOfWorkHours> getTypesOfWorkHours() {
        return typeOfWorkHoursDAO.list(TypeOfWorkHours.class);
    }

    @Override
    @Transactional(readOnly = true)
    public void initCreate() {
        this.typeOfWorkHours = TypeOfWorkHours.create("", "");
        typeOfWorkHours.setCodeAutogenerated(configurationDAO
                .getConfiguration()
                .getGenerateCodeForTypesOfWorkHours());
        if (!typeOfWorkHours.isCodeAutogenerated()) {
            typeOfWorkHours.setCode("");
        } else {
            setDefaultCode();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void initEdit(TypeOfWorkHours typeOfWorkHours) {
        try {
            this.typeOfWorkHours = typeOfWorkHoursDAO.find(typeOfWorkHours
                    .getId());
            initOldCodes();
        } catch (InstanceNotFoundException e) {
            initCreate();
        }
    }

    @Override
    public EntityNameEnum getEntityName() {
        return EntityNameEnum.WORK_HOURS_TYPE;
    }

    @Override
    public Set<IntegrationEntity> getChildren() {
        return new HashSet<IntegrationEntity>();
    }

    @Override
    public IntegrationEntity getCurrentEntity() {
        return this.typeOfWorkHours;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsCostCategoriesUsing(TypeOfWorkHours typeOfWorkHours) {
        return hourCostDAO.hoursCostsByTypeOfWorkHour(typeOfWorkHours).isEmpty();
    }

}