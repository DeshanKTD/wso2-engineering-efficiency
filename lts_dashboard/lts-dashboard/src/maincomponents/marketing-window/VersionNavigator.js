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
import {withStyles} from 'material-ui/styles';
import Input, {InputLabel} from 'material-ui/Input';
import {MenuItem} from 'material-ui/Menu';
import {FormControl} from 'material-ui/Form';
import Select from 'material-ui/Select';
import axios from "axios/index";
import {getServer} from "../../resources/util";

const styles = theme => ({
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    formControl: {
        margin: theme.spacing.unit,
        width: `100%`,
    },
    selectEmpty: {
        marginTop: theme.spacing.unit * 2,
    },
    progress: {
        margin: `0 ${theme.spacing.unit * 2}px`,
        paddingTop: 10
    },
});

class VersionNavigator extends React.Component {

    handleChange = event => {
        this.setState({[event.target.name]: event.target.value},
            () => {
                let versionName = this.getVersionName(event.target.value);
                this.props.setVersion(this.state.version,versionName);
            });
    };

    constructor(props) {
        super(props);
        this.state = {
            version: '',
            versionList: [],
            issueLoading: false
        };
    }

    fetchVersions(productId) {
        let data = {
            productId : productId
        };
        if (productId !== '') {
            this.setState({
                issueLoading:true
            },()=>(
                axios.post(getServer()+'/products/versions',
                    data
                ).then(
                    (response) => {
                        let datat = response.data;
                        this.setState(
                            {
                                versionList: datat.sort().reverse(),
                                issueLoading:false
                            }
                        );
                    }
                )
            ));
        }
    }

    componentWillUpdate(nextProps, nextState) {
        if (nextProps.product !== this.props.product) {
            this.fetchVersions(nextProps.product);
        }
    }

    getVersionName(versionId){
        let versionName= null;
        this.state.versionList.map(function (versionData) {
            if(versionId==versionData["versionId"]){
                versionName = versionData["versionName"]
            }
        });
        return versionName;
    }

    render() {
        const {classes} = this.props;

        return (
            <div>
                <form className={classes.container} autoComplete="off">
                    <FormControl className={classes.formControl}>
                        <InputLabel htmlFor="version-simple">Version</InputLabel>
                        <Select
                            value={this.state.version}
                            onChange={this.handleChange}
                            input={<Input name="version" id="version-simple"/>}
                        >
                            <MenuItem value="">
                                <em>None</em>
                            </MenuItem>
                            {
                                this.state.versionList.map((versionData, index) => (
                                    <MenuItem key={index} value={versionData["versionId"]}>{versionData["versionName"]}</MenuItem>
                                ))
                            }
                        </Select>
                    </FormControl>
                </form>

            </div>
        );
    }
}

VersionNavigator.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(VersionNavigator);