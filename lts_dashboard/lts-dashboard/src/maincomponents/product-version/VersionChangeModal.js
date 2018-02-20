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
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import Modal from 'material-ui/Modal';
import Button from 'material-ui/Button';
import FormControl from "material-ui/es/Form/FormControl";
import InputLabel from "material-ui/es/Input/InputLabel";
import Input from "material-ui/es/Input/Input";
import {getServer} from "../../resources/util";
import axios from "axios/index";


function getModalStyle() {

    return {
        top: `${40}%`,
        left: `${40}%`,
    };
}

const styles = theme => ({
    paper: {
        position: 'absolute',
        width: theme.spacing.unit * 60,
        backgroundColor: theme.palette.background.paper,
        boxShadow: theme.shadows[5],
        padding: theme.spacing.unit * 4,
    },

    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    formControl: {
        margin: theme.spacing.unit,
    },
});

class VersionChangeModal extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            open: false,
            versionName: "",
        };
    }

    handleOpen = () => {
        this.setState({ open: true });
    };

    handleClose = () => {
        this.setState({ open: false });
    };

    componentWillUpdate(nextProps, nextState) {
        if (nextProps.open !== this.state.open) {
            this.setState({
                open : !this.state.open
            });
        }
    }

    handleChange = event => {
        this.setState({ versionName: event.target.value });
    };


    changeVersion = () => {
        if(this.validateVersion(this.state.versionName,this.props.versionList)) {
            let data = {
                productId: this.props.productId,
                versionId: this.props.versionId,
                versionName: this.props.versionName
            };

            // axios.post('http://' + getServer() + '/lts/products/versions/add', data
            // ).then(
            //     (response) => {
            //         let datat = response.data;
            //         console.log(datat);
            //         this.setState({
            //             open: false,
            //             versionName:""
            //         });
            //         this.props.fetchVersions(this.props.productId);
            //     }
            // )
        }
    };

    validateVersion(version,versionList){
        let isValid = true;
        if(version==""){
            isValid = false;
        } else{
            versionList.forEach(function (versionExisting) {
                if( versionExisting["versionName"]==version){
                    isValid = false;
                }
            })
        }

        return isValid;
    }

    render() {
        const { classes } = this.props;

        return (
            <div>
                <Modal
                    aria-labelledby="simple-modal-title"
                    aria-describedby="simple-modal-description"
                    open={this.state.open}
                    onClose={this.handleClose}
                >
                    <div style={getModalStyle()} className={classes.paper}>
                        <div className={classes.container}>
                            <FormControl className={classes.formControl}>
                                <InputLabel htmlFor="versionName">Version</InputLabel>
                                <Input id="versionName" value={this.state.versionName} onChange={this.handleChange} />
                            </FormControl>
                            <FormControl className={classes.formControl}>
                                <Button onClick={this.changeVersion}>Change Version</Button>
                            </FormControl>
                        </div>



                    </div>
                </Modal>
            </div>
        );
    }
}

VersionChangeModal.propTypes = {
    classes: PropTypes.object.isRequired,
};

// We need an intermediary variable for handling the recursive nesting.
const SimpleModalWrapped = withStyles(styles)(VersionChangeModal);

export default SimpleModalWrapped;