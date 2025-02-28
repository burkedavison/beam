/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

import '../../controllers/playground_controller.dart';
import 'output_tab.dart';

class OutputTabs extends StatelessWidget {
  final PlaygroundController playgroundController;
  final TabController tabController;

  const OutputTabs({
    super.key,
    required this.playgroundController,
    required this.tabController,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 300,
      child: TabBar(
        controller: tabController,
        tabs: <Widget>[
          OutputTab(
            playgroundController: playgroundController,
            name: 'widgets.output.result'.tr(),
            isSelected: tabController.index == 0,
            maxPossibleContent: playgroundController.codeRunner.resultLogOutput,
            hasFilter: true,
          ),
          if (playgroundController.graphAvailable)
            OutputTab(
              playgroundController: playgroundController,
              name: 'widgets.output.graph'.tr(),
              isSelected: tabController.index == 2,
              maxPossibleContent:
                  playgroundController.codeRunner.result?.graph ?? '',
            ),
        ],
      ),
    );
  }
}
