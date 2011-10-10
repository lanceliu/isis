/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.runtimes.dflt.objectstores.sql.jdbc;

import org.apache.isis.core.commons.debug.DebugBuilder;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.core.metamodel.spec.feature.OneToOneAssociation;
import org.apache.isis.runtimes.dflt.objectstores.sql.DatabaseConnector;
import org.apache.isis.runtimes.dflt.objectstores.sql.Results;
import org.apache.isis.runtimes.dflt.objectstores.sql.Sql;
import org.apache.isis.runtimes.dflt.objectstores.sql.mapping.FieldMapping;
import org.apache.isis.runtimes.dflt.objectstores.sql.mapping.FieldMappingFactory;

public class JdbcObjectReferenceFieldMapping extends JdbcObjectReferenceMapping implements FieldMapping {

    public static class Factory implements FieldMappingFactory {
        @Override
        public FieldMapping createFieldMapping(final ObjectAssociation field) {
            if (field.getSpecification().isAbstract()) {
                return new JdbcAbstractReferenceFieldMapping(field);
            }
            return new JdbcObjectReferenceFieldMapping(field);
        }
    }

    protected final ObjectAssociation field;

    @Override
    public void appendWhereClause(final DatabaseConnector connector, final StringBuffer sql, final ObjectAdapter object) {
        final ObjectAdapter fieldValue = field.get(object);
        appendWhereClause(connector, sql, fieldValue.getOid());
    }

    @Override
    public void appendWhereObject(DatabaseConnector connector, ObjectAdapter objectAdapter) {
        final ObjectAdapter fieldValue = field.get(objectAdapter);
        connector.addToQueryValues(primaryKeyAsObject(fieldValue.getOid()));
    }

    public JdbcObjectReferenceFieldMapping(final ObjectAssociation field) {
        super(columnName(field), field.getSpecification());
        this.field = field;
    }

    private static String columnName(final ObjectAssociation field) {
        return Sql.sqlFieldName(field.getId());
    }

    @Override
    public void appendInsertValues(final DatabaseConnector connector, final StringBuffer sb, final ObjectAdapter object) {
        final ObjectAdapter fieldValue = field.get(object);
        super.appendInsertValues(connector, sb, fieldValue);
    }

    @Override
    public void appendUpdateValues(final DatabaseConnector connector, final StringBuffer sql, final ObjectAdapter object) {
        final ObjectAdapter fieldValue = field.get(object);
        super.appendUpdateValues(connector, sql, fieldValue);
    }

    @Override
    public void initializeField(final ObjectAdapter object, final Results rs) {
        final ObjectAdapter reference = initializeField(rs);
        ((OneToOneAssociation) field).initAssociation(object, reference);
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        debug.appendln(field.getId(), getColumn());
    }

}