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
import Dialog from "material-ui/es/Dialog/Dialog";
import DialogTitle from "material-ui/es/Dialog/DialogTitle";
import DialogActions from "material-ui/es/Dialog/DialogActions";
import Button from "material-ui/es/Button/Button";
import {getServer} from "../../resources/util";
import axios from "axios/index";


class VersionDeleteDialog extends React.Component {

    handleClose = () => {
        this.setState({open: false});
    };

    deleteVersion = () => {
        this.deleteData(this.state.versionId);
    };

    deleteData = (versionId) => {
        let data = {
            versionId: versionId
        };
        axios.post('http://' + getServer() + '/lts/products/deleteVersion', data
        ).then(
            (response) => {
                this.props.fetchVersions(this.state.productId);
                this.setState(
                    {
                        versionId: "",
                        versionName: "",
                        productId: ""
                    },
                );
            }
        )
    };

    constructor(props) {
        super(props);

        this.state = {
            open: false,
            versionName: "",
            versionId: "",
            productId: ""
        }
    }

    componentWillUpdate(nextProps, nextState) {
        if (nextProps.open !== this.state.open) {
            this.setState({
                open: !this.state.open,
                versionName: nextProps.deleteData.versionName,
                versionId: nextProps.deleteData.versionId,
                productId: nextProps.productId
            });
        }
    }

    render() {
        return (
            <Dialog
                open={this.state.open}
                onClose={this.handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle
                    id="alert-dialog-title">{"Do you want to delete the version " + this.state.versionName + " ?"}</DialogTitle>
                <DialogActions>
                    <Button onClick={this.deleteVersion} color="primary" autoFocus>
                        Yes
                    </Button>
                    <Button onClick={this.handleClose} color="primary">
                        No
                    </Button>
                </DialogActions>
            </Dialog>
        )
    }
}


export default VersionDeleteDialog;