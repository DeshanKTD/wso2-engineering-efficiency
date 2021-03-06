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


import React, {Component} from 'react';
import { ListItem,  ListItemText } from 'material-ui/List';


class RepoNameItem extends Component {
    constructor(props) {
        super(props);
        this.state = {
            repoId: this.props.repoObject.repoId,
            repoName: this.props.repoObject.repoName,
            repoUrl: this.props.repoObject.repoUrl
        };

        this.getBranches = this.getBranches.bind(this);
    }


    getBranches() {
        this.props.fetchBranches(this.state.repoUrl,this.state.repoName,this.state.repoId);
    }

    render() {
        return (

            <ListItem divider={true} disableGutters={true} button onClick={this.getBranches}>
                <ListItemText inset primary={this.state.repoName}/>
            </ListItem>

        );
    }
}

export default RepoNameItem;