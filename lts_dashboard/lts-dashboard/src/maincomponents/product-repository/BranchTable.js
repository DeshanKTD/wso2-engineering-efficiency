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
import Button from "material-ui/es/Button/Button";
import BranchVersionChangeButton from "./BranchVersionChangeButton";



export default class BranchTable extends React.PureComponent {
    constructor(props) {
        super(props);

        this.state = {
            columns: [
                { name: 'branchName', title: 'Branch' },
                { name: 'version', title: 'Version' },
                { name: 'button', title: '_' },
            ],
            rows:[],
            branchList:[]
        };
        this.openBranchChangeWindow = this.openBranchChangeWindow.bind(this);
    }


    openBranchChangeWindow(data){
        this.props.openBranchVersionAddWindow(data);
    }

    createBranchTableData = (branchList) => {
        let newRows = [];
        let callback = this.openBranchChangeWindow
        branchList.forEach(function (branch) {
            let change = true;
            if(branch["branchId"]==-1){
                change=false;
            }
            let obj = {
                branchName : branch["branchName"],
                version : branch["versionName"],
                button : <BranchVersionChangeButton
                    change={change}
                    versionId={branch["versionId"]}
                    branchId={branch["branchId"]}
                    branchName={branch["branchName"]}
                    openBranchChangeWindow={callback}
                />
            };
            newRows.push(obj);
        });

        return newRows;
    };


    componentWillUpdate(nextProps, nextState) {
        if (nextProps.branchList !== this.state.branchList) {
            this.setState({
                branchList: nextProps.branchList,
                rows: this.createBranchTableData(nextProps.branchList)
            });
        }
    }

    render() {

        return (
            <Paper>
                <Grid
                    rows={this.state.rows}
                    columns={this.state.columns}
                >
                    <Table />
                    <TableHeaderRow />
                </Grid>
            </Paper>
        );
    }
}