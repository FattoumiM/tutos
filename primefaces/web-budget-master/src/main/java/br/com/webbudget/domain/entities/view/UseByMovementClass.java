/*
 * Copyright (C) 2019 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.domain.entities.view;

import br.com.webbudget.application.components.dto.Color;
import br.com.webbudget.domain.entities.ImmutableEntity;
import br.com.webbudget.domain.entities.registration.MovementClass;
import br.com.webbudget.domain.entities.registration.MovementClassType;
import br.com.webbudget.infrastructure.jpa.ColorConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.math.BigDecimal;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL;
import static javax.persistence.EnumType.STRING;

/**
 * Immutable entity to represent the view with the daily use grouped by {@link MovementClass}
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 3.0.0, 28/04/2019
 */
@Entity
@Immutable
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "wb_view_009", schema = FINANCIAL)
public class UseByMovementClass extends ImmutableEntity {

    @Getter
    @Column(name = "financial_period_id")
    private Long financialPeriodId;
    @Getter
    @Column(name = "financial_period")
    private String financialPeriod;
    @Getter
    @Column(name = "cost_center_color")
    @Convert(converter = ColorConverter.class)
    private Color costCenterColor;
    @Getter
    @Column(name = "cost_center_id")
    private Long costCenterId;
    @Getter
    @Column(name = "cost_center")
    private String costCenter;
    @Getter
    @Column(name = "movement_class_id")
    private Long movementClassId;
    @Getter
    @Column(name = "movement_class")
    private String movementClass;
    @Getter
    @Enumerated(STRING)
    @Column(name = "direction")
    private MovementClassType direction;
    @Getter
    @Column(name = "total_paid")
    private BigDecimal value;
}
