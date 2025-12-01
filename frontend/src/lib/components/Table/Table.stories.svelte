<script module lang="ts">
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import Table from './Table.svelte';
  import Chip from '$components/Chip/Chip.svelte';
	import { fn } from 'storybook/test';

  interface User {
    id: number;
    name: string;
    age: number;
    active: boolean;
  }

  const users: User[] = [
    { id: 1, name: "Tobi", age: 23, active: true },
    { id: 2, name: "Jose", age: 22, active: false },
    { id: 3, name: "Tobi", age: 23, active: true },
    { id: 4, name: "Jose", age: 22, active: false },
  ];

  const columns = [
    { id: "name", label: "User" },
    { id: "age", label: "Age" },
    {
      id: "active",
      label: "Status",

      render: (row: User) =>
        row.active
          ? `<span class="text-green-600 font-bold">Active</span>`
          : `<span class="text-red-600 font-bold">Inactive</span>`
    }
  ];

  const columnsWithComponent = [
    { id: "name", label: "User" },
    { id: "age", label: "Age" },
    {
      id: "active",
      label: "Status",

      render: (row: User) =>
        row.active
          ? { component: Chip, props: { variant: "success", label: "Active" } }
          : { component: Chip, props: { variant: "destructive", label: "Inactive" } }
    }
  ];

  // More on how to set up stories at: https://storybook.js.org/docs/writing-stories
  const { Story } = defineMeta({
    title: 'lib/Table',
    component: Table,
    tags: ['autodocs'],
    argTypes: {
      rows: { control: 'object' },
      columns: { control: 'object' },
      striped: { control: 'boolean' },
      bordered: { control: 'boolean' },
      hover: { control: 'boolean' },
      skeleton: { control: 'boolean' },
      emptyMessage: { control: 'text' },
      class: { control: 'text' },
    },
    args: {
      rows: users
    }
  });
</script>

<!-- More on writing stories with args: https://storybook.js.org/docs/writing-stories/args -->
<Story name="Default" args={{ columns}} />

<Story name="With Striped Rows" args={{ columns, striped: true }} />

<Story name="With Border" args={{ columns, bordered: true }} />

<Story name="With Hover" args={{ columns, hover: true }} />

<Story name="With Skeleton" args={{ columns, skeleton: true }} />

<Story name="With Component Render" args={{ columns: columnsWithComponent, hover: true }} />

<Story name="Clickeable Rows" args={{ columns, hover: true, onRowClick: fn() }} />

<Story name="Empty Table" args={{ columns, rows: [] }} />

<Story name="Custom Empty Message" args={{ columns, rows: [], emptyMessage: 'No users found.' }} />
