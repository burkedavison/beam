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
package org.apache.beam.sdk.io.gcp.bigtable;

import static org.apache.beam.vendor.guava.v26_0_jre.com.google.common.base.Preconditions.checkArgument;

import com.google.auto.value.AutoValue;
import com.google.cloud.bigtable.config.BigtableOptions;
import com.google.cloud.bigtable.config.CredentialOptions;
import java.io.Serializable;
import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.vendor.guava.v26_0_jre.com.google.common.annotations.VisibleForTesting;
import org.apache.beam.vendor.guava.v26_0_jre.com.google.common.base.MoreObjects;
import org.checkerframework.checker.nullness.qual.Nullable;

/** Configuration for a Cloud Bigtable client. */
@AutoValue
@SuppressWarnings({
  "nullness" // TODO(https://github.com/apache/beam/issues/20497)
})
public abstract class BigtableConfig implements Serializable {

  /** Returns the project id being written to. */
  public abstract @Nullable ValueProvider<String> getProjectId();

  /** Returns the instance id being written to. */
  public abstract @Nullable ValueProvider<String> getInstanceId();

  /** Returns the table being read from. */
  public abstract @Nullable ValueProvider<String> getTableId();

  /** Returns the app profile being read from. */
  public abstract @Nullable ValueProvider<String> getAppProfileId();

  /**
   * Returns the Google Cloud Bigtable instance being written to, and other parameters.
   *
   * @deprecated will be replaced by bigtable options configurator.
   */
  @Deprecated
  abstract @Nullable BigtableOptions getBigtableOptions();

  /** Configurator of the effective Bigtable Options. */
  abstract @Nullable SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder>
      getBigtableOptionsConfigurator();

  /** Weather validate that table exists before writing. */
  abstract boolean getValidate();

  /** {@link BigtableService} used only for testing. */
  abstract @Nullable BigtableService getBigtableService();

  /** Bigtable emulator. Used only for testing. */
  abstract @Nullable String getEmulatorHost();

  abstract Builder toBuilder();

  static BigtableConfig.Builder builder() {
    return new AutoValue_BigtableConfig.Builder();
  }

  @AutoValue.Builder
  abstract static class Builder {

    abstract Builder setProjectId(ValueProvider<String> projectId);

    abstract Builder setInstanceId(ValueProvider<String> instanceId);

    abstract Builder setTableId(ValueProvider<String> tableId);

    abstract Builder setAppProfileId(ValueProvider<String> appProfileId);

    /** @deprecated will be replaced by bigtable options configurator. */
    @Deprecated
    abstract Builder setBigtableOptions(BigtableOptions options);

    abstract Builder setValidate(boolean validate);

    abstract Builder setBigtableOptionsConfigurator(
        SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder> optionsConfigurator);

    abstract Builder setBigtableService(BigtableService bigtableService);

    abstract Builder setEmulatorHost(String emulatorHost);

    abstract BigtableConfig build();
  }

  public BigtableConfig withProjectId(ValueProvider<String> projectId) {
    checkArgument(projectId != null, "Project Id of BigTable can not be null");
    return toBuilder().setProjectId(projectId).build();
  }

  public BigtableConfig withInstanceId(ValueProvider<String> instanceId) {
    checkArgument(instanceId != null, "Instance Id of BigTable can not be null");
    return toBuilder().setInstanceId(instanceId).build();
  }

  public BigtableConfig withTableId(ValueProvider<String> tableId) {
    checkArgument(tableId != null, "tableId can not be null");
    return toBuilder().setTableId(tableId).build();
  }

  public BigtableConfig withAppProfileId(ValueProvider<String> appProfileId) {
    checkArgument(appProfileId != null, "tableId can not be null");
    return toBuilder().setAppProfileId(appProfileId).build();
  }

  /** @deprecated will be replaced by bigtable options configurator. */
  @Deprecated
  public BigtableConfig withBigtableOptions(BigtableOptions options) {
    checkArgument(options != null, "Bigtable options can not be null");
    return toBuilder().setBigtableOptions(options).build();
  }

  public BigtableConfig withBigtableOptionsConfigurator(
      SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder> configurator) {
    checkArgument(configurator != null, "configurator can not be null");
    return toBuilder().setBigtableOptionsConfigurator(configurator).build();
  }

  public BigtableConfig withValidate(boolean isEnabled) {
    return toBuilder().setValidate(isEnabled).build();
  }

  @VisibleForTesting
  public BigtableConfig withBigtableService(BigtableService bigtableService) {
    checkArgument(bigtableService != null, "bigtableService can not be null");
    return toBuilder().setBigtableService(bigtableService).build();
  }

  @VisibleForTesting
  public BigtableConfig withEmulator(String emulatorHost) {
    checkArgument(emulatorHost != null, "emulatorHost can not be null");
    return toBuilder().setEmulatorHost(emulatorHost).build();
  }

  void validate() {
    checkArgument(
        getTableId() != null && (!getTableId().isAccessible() || !getTableId().get().isEmpty()),
        "Could not obtain Bigtable table id");

    checkArgument(
        (getProjectId() != null
                && (!getProjectId().isAccessible() || !getProjectId().get().isEmpty()))
            || (getBigtableOptions() != null
                && getBigtableOptions().getProjectId() != null
                && !getBigtableOptions().getProjectId().isEmpty()),
        "Could not obtain Bigtable project id");

    checkArgument(
        (getInstanceId() != null
                && (!getInstanceId().isAccessible() || !getInstanceId().get().isEmpty()))
            || (getBigtableOptions() != null
                && getBigtableOptions().getInstanceId() != null
                && !getBigtableOptions().getInstanceId().isEmpty()),
        "Could not obtain Bigtable instance id");
  }

  void populateDisplayData(DisplayData.Builder builder) {
    builder
        .addIfNotNull(
            DisplayData.item("projectId", getProjectId()).withLabel("Bigtable Project Id"))
        .addIfNotNull(
            DisplayData.item("instanceId", getInstanceId()).withLabel("Bigtable Instance Id"))
        .addIfNotNull(DisplayData.item("tableId", getTableId()).withLabel("Bigtable Table Id"))
        .addIfNotNull(
            DisplayData.item("appProfileId", getAppProfileId())
                .withLabel("Bigtable App Profile Id"))
        .add(DisplayData.item("withValidation", getValidate()).withLabel("Check is table exists"));

    if (getBigtableOptions() != null) {
      builder.add(
          DisplayData.item("bigtableOptions", getBigtableOptions().toString())
              .withLabel("Bigtable Options"));
    }
  }

  /**
   * Helper function that either returns the mock Bigtable service supplied by {@link
   * #withBigtableService} or creates and returns an implementation that talks to {@code Cloud
   * Bigtable}.
   *
   * <p>Also populate the credentials option from {@link GcpOptions#getGcpCredential()} if the
   * default credentials are being used on {@link BigtableOptions}.
   */
  @VisibleForTesting
  BigtableService getBigtableService(PipelineOptions pipelineOptions) {
    if (getBigtableService() != null) {
      return getBigtableService();
    }

    BigtableOptions.Builder bigtableOptions = effectiveUserProvidedBigtableOptions();

    bigtableOptions.setUserAgent(pipelineOptions.getUserAgent());

    if (bigtableOptions.build().getCredentialOptions().getCredentialType()
        == CredentialOptions.CredentialType.DefaultCredentials) {
      bigtableOptions.setCredentialOptions(
          CredentialOptions.credential(pipelineOptions.as(GcpOptions.class).getGcpCredential()));
    }

    return new BigtableServiceImpl(bigtableOptions.build());
  }

  boolean isDataAccessible() {
    return getTableId().isAccessible()
        && (getProjectId() == null || getProjectId().isAccessible())
        && (getInstanceId() == null || getInstanceId().isAccessible());
  }

  private BigtableOptions.Builder effectiveUserProvidedBigtableOptions() {
    BigtableOptions.Builder effectiveOptions =
        getBigtableOptions() != null
            ? getBigtableOptions().toBuilder()
            : new BigtableOptions.Builder();

    if (getBigtableOptionsConfigurator() != null) {
      effectiveOptions = getBigtableOptionsConfigurator().apply(effectiveOptions);
    }

    // Default option that should be forced in most cases
    effectiveOptions.setUseCachedDataPool(true);

    if (getInstanceId() != null) {
      effectiveOptions.setInstanceId(getInstanceId().get());
    }

    if (getProjectId() != null) {
      effectiveOptions.setProjectId(getProjectId().get());
    }

    if (getEmulatorHost() != null) {
      effectiveOptions.enableEmulator(getEmulatorHost());
      effectiveOptions.setUseCachedDataPool(false);
    }

    return effectiveOptions;
  }

  @Override
  public final String toString() {
    return MoreObjects.toStringHelper(BigtableConfig.class)
        .add("projectId", getProjectId())
        .add("instanceId", getInstanceId())
        .add("tableId", getTableId())
        .add("appProfileId", getAppProfileId())
        .add(
            "bigtableOptionsConfigurator",
            getBigtableOptionsConfigurator() == null
                ? null
                : getBigtableOptionsConfigurator().getClass().getName())
        .add("options", getBigtableOptions())
        .toString();
  }
}
