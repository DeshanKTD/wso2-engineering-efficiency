/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import React from 'react';
import Paper from 'material-ui/Paper';
import {
    FilteringState,
    IntegratedFiltering,
} from '@devexpress/dx-react-grid';
import {
    Grid,
    Table,
    TableHeaderRow,
    TableFilterRow,
} from '@devexpress/dx-react-grid-material-ui';
import VersionChangeButton from "./VersionChangeButton";
import VersionDeleteButton from "./VersionDeleteButton";


export default class VersionTable extends React.PureComponent {
    constructor(props) {
        super(props);

        this.state = {
            columns: [
                {name: 'version', title: 'Version'},
                {name: 'button', title: '_'},
                {name: 'deleteButton', title: '_'}
            ],
            rows: [],
        };

        this.openDeleteDialog = this.openDeleteDialog.bind(this);
        this.openChangeDialog = this.openChangeDialog.bind(this);
        this.createRows = this.createRows.bind(this);
    }


    createRows(versionData) {
        let row = [];
        let openDeleteDiag = this.openDeleteDialog;
        let openChangeDiag = this.openChangeDialog;
        versionData.forEach(function (version) {
            let obj = {
                version: version["versionName"],
                button: <VersionChangeButton
                    id={version["versionId"]}
                    name={version["versionName"]}
                    openChangeWindow={openChangeDiag}
                />,
                deleteButton : <VersionDeleteButton
                    id={version["versionId"]}
                    name={version["versionName"]}
                    openDeleteDialog={openDeleteDiag}
                />
            };
            row.push(obj);
        });

        return row;
    }


    openDeleteDialog(versionId,versionName){
        this.props.openDeleteWindow(versionId,versionName);
    }

    openChangeDialog(versionId,versionName){
        this.props.openChangeWindow(versionId,versionName);
    }


    componentWillUpdate(nextProps, nextState) {
        if (nextProps.tableData !== this.state.tableData) {
            this.setState({
                tableData: nextProps.tableData,
                rows: this.createRows(nextProps.tableData)
            });
        }
    }


    render() {
        const {rows, columns} = this.state;

        return (
            <div>
                <Paper>
                    <Grid
                        rows={rows}
                        columns={columns}
                    >
                        <Table/>
                        <TableHeaderRow/>
                    </Grid>
                </Paper>
            </div>
        );
    }
}