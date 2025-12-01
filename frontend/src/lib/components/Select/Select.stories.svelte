<script module lang="ts">
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import Select from './Select.svelte';
	import Icon from '$components/Icon/Icon.svelte';

  const { Story } = defineMeta({
    title: 'lib/Select',
    component: Select,
    tags: ['autodocs'],
    argTypes: {
      label: { control: 'text' },
      options: { control: 'object' },
      value: { control: 'text' },
      disabled: { control: 'boolean' },
      skeleton: { control: 'boolean' },
      placeholder: { control: 'text' },
      class: { control: 'text' },
      onchange: { action: 'onchange' },
    },
    args: {
      onchange: (current) => { console.log(current); },
    }
  });
</script>

{#snippet icon()}
  <Icon name="eye" class="inline-block w-4 h-4 mr-2 align-middle" />
{/snippet}

<Story name="Default" args={{ options: [{ value: '1', label: 'Option 1', icon }, { value: '2', label: 'Option 2' }] }} />

<Story name="With Label" args={{ label: 'Choose an option:', options: [{ value: '1', label: 'Option 1', icon }, { value: '2', label: 'Option 2' }] }} />

<Story name="Disabled" args={{ disabled: true, options: [{ value: '1', label: 'Option 1' }, { value: '2', label: 'Option 2' }] }} />

<Story name="Skeleton" args={{ skeleton: true, options: [{ value: '1', label: 'Option 1' }, { value: '2', label: 'Option 2' }] }} />

<Story name="With Placeholder" args={{ placeholder: 'Please select...', options: [{ value: '1', label: 'Option 1' }, { value: '2', label: 'Option 2' }] }} />
